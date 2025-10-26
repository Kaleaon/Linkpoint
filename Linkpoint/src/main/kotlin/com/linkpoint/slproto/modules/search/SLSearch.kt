package com.linkpoint.slproto.modules.search

import com.google.common.base.Objects
import com.linkpoint.Debug
import com.linkpoint.dao.SearchGridResult
import com.linkpoint.dao.SearchGridResultDao
import com.linkpoint.react.AsyncLimitsRequestHandler
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.messages.DirFindQuery
import com.linkpoint.slproto.messages.DirGroupsReply
import com.linkpoint.slproto.messages.DirPeopleReply
import com.linkpoint.slproto.messages.DirPlacesQuery
import com.linkpoint.slproto.messages.DirPlacesReply
import com.linkpoint.slproto.messages.ParcelInfoReply
import com.linkpoint.slproto.messages.ParcelInfoRequest
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.modules.search.SearchGridQuery
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.LevensteinDistance
import com.linkpoint.utils.UUIDPool
import de.greenrobot.dao.query.LazyList
import de.greenrobot.dao.query.WhereCondition
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull

class SLSearch : SLModule() {
    private const val DFQ_ADULT_SIMS_ONLY: Int = 134217728
    private const val DFQ_AGENT_OWNED: Int = 64
    private const val DFQ_AREA_SORT: Int = 262144
    private const val DFQ_AUCTION: Int = 512
    private const val DFQ_DATE_EVENTS: Int = 32
    private const val DFQ_DWELL_SORT: Int = 1024
    private const val DFQ_EVENTS: Int = 8
    private const val DFQ_FILTER_MATURE: Int = 4194304
    private const val DFQ_FOR_SALE: Int = 128
    private const val DFQ_GROUPS: Int = 16
    private const val DFQ_GROUP_OWNED: Int = 256
    private const val DFQ_INC_ADULT: Int = 67108864
    private const val DFQ_INC_MATURE: Int = 33554432
    private const val DFQ_INC_NEW_VIEWER: Int = 117440512
    private const val DFQ_INC_PG: Int = 16777216
    private const val DFQ_LIMIT_BY_AREA: Int = 2097152
    private const val DFQ_LIMIT_BY_PRICE: Int = 1048576
    private const val DFQ_MATURE_SIMS_ONLY: Int = 16384
    private const val DFQ_NAME_SORT: Int = 524288
    private const val DFQ_ONLINE: Int = 2
    private const val DFQ_PEOPLE: Int = 1
    private const val DFQ_PER_METER_SORT: Int = 131072
    private const val DFQ_PG_EVENTS_ONLY: Int = 8192
    private const val DFQ_PG_PARCELS_ONLY: Int = 8388608
    private const val DFQ_PG_SIMS_ONLY: Int = 2048
    private const val DFQ_PICTURES_ONLY: Int = 4096
    private const val DFQ_PLACES: Int = 4
    private const val DFQ_PRICE_SORT: Int = 65536
    private const val DFQ_SORT_ASC: Int = 32768
    /* access modifiers changed from: private */
    val AtomicReference<SearchGridQuery> currentSearchQuery = AtomicReference<>((Object) null)
    private val RequestHandler<UUID> parcelInfoRequestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(uuid: UUID) {
            Debug.Printf("ParcelInfo: Requesting for %s", uuid)
            val parcelInfoRequest: ParcelInfoRequest = ParcelInfoRequest()
            parcelInfoRequest.AgentData_Field.AgentID = SLSearch.this.circuitInfo.agentID
            parcelInfoRequest.AgentData_Field.SessionID = SLSearch.this.circuitInfo.sessionID
            parcelInfoRequest.Data_Field.ParcelID = uuid
            parcelInfoRequest.isReliable = true
            SLSearch.this.SendMessage(parcelInfoRequest)
        }
    }, false, 3, 15000)
    private val ResultHandler<UUID, ParcelInfoReply> parcelInfoResultHandler
    private val RequestHandler<SearchGridQuery> searchRequestHandler = AsyncRequestHandler(this.agentCircuit, SimpleRequestHandler<SearchGridQuery>() {

        /* renamed from: -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
        private const val /* synthetic */ IntArray f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = null
        final /* synthetic */ IntArray $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$modules$search$SearchGridQuery$SearchType

        /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
        @JvmStatic
private /* synthetic */ IntArray m238getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues() {
            if (f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues != null) {
                return f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues
            }
            val iArr: IntArray = Int[SearchGridQuery.SearchType.values().length]
            try {
                iArr[SearchGridQuery.SearchType.Groups.ordinal()] = 1
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[SearchGridQuery.SearchType.People.ordinal()] = 2
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[SearchGridQuery.SearchType.Places.ordinal()] = 3
            } catch (NoSuchFieldError e3) {
            }
            f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = iArr
            return iArr
        }

        fun onRequest(searchGridQuery: SearchGridQuery) {
            SLSearch.this.currentSearchQuery.set(searchGridQuery)
            switch (m238getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues()[searchGridQuery.searchType().ordinal()]) {
                case 1:
                    SLSearch.this.SearchGroups(searchGridQuery.searchText(), searchGridQuery.searchUUID())
                    return
                case 2:
                    SLSearch.this.SearchPeople(searchGridQuery.searchText(), searchGridQuery.searchUUID())
                    return
                case 3:
                    SLSearch.this.SearchPlaces(searchGridQuery.searchText(), searchGridQuery.searchUUID())
                    return
                default:
                    return
            }
        }
    private val ResultHandler<SearchGridQuery, LazyList<SearchGridResult>> searchResultHandler
    private val UserManager userManager

    public SLSearch(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID())
        if (this.userManager != null) {
            this.searchResultHandler = this.userManager.getSearchManager().searchResults().attachRequestHandler(this.searchRequestHandler)
            this.parcelInfoResultHandler = this.userManager.parcelInfoData().getRequestSource().attachRequestHandler(this.parcelInfoRequestHandler)
            return
        }
        this.searchResultHandler = null
        this.parcelInfoResultHandler = null
    }

    /* access modifiers changed from: private */
    fun SearchGroups(str: String, uuid: UUID) {
        val dirFindQuery: DirFindQuery = DirFindQuery()
        dirFindQuery.AgentData_Field.AgentID = this.circuitInfo.agentID
        dirFindQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID
        dirFindQuery.QueryData_Field.QueryID = uuid
        dirFindQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(str)
        dirFindQuery.QueryData_Field.QueryFlags = 117440528
        dirFindQuery.QueryData_Field.QueryStart = 0
        dirFindQuery.isReliable = true
        SendMessage(dirFindQuery)
    }

    /* access modifiers changed from: private */
    fun SearchPeople(str: String, uuid: UUID) {
        val dirFindQuery: DirFindQuery = DirFindQuery()
        dirFindQuery.AgentData_Field.AgentID = this.circuitInfo.agentID
        dirFindQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID
        dirFindQuery.QueryData_Field.QueryID = uuid
        dirFindQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(str)
        dirFindQuery.QueryData_Field.QueryFlags = 117440513
        dirFindQuery.QueryData_Field.QueryStart = 0
        dirFindQuery.isReliable = true
        SendMessage(dirFindQuery)
    }

    /* access modifiers changed from: private */
    fun SearchPlaces(str: String, uuid: UUID) {
        val dirPlacesQuery: DirPlacesQuery = DirPlacesQuery()
        dirPlacesQuery.AgentData_Field.AgentID = this.circuitInfo.agentID
        dirPlacesQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID
        dirPlacesQuery.QueryData_Field.QueryID = uuid
        dirPlacesQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(str)
        dirPlacesQuery.QueryData_Field.QueryFlags = 117440516
        dirPlacesQuery.QueryData_Field.QueryStart = 0
        dirPlacesQuery.QueryData_Field.SimName = SLMessage.stringToVariableOEM("")
        dirPlacesQuery.isReliable = true
        SendMessage(dirPlacesQuery)
    }

     private fun updateSearchResults(searchGridResultDao: SearchGridResultDao, searchGridQuery: SearchGridQuery) {
        if (this.searchResultHandler != null) {
            this.searchResultHandler.onResultData(searchGridQuery, searchGridResultDao.queryBuilder().where(SearchGridResultDao.Properties.SearchUUID.eq(searchGridQuery.searchUUID()), WhereCondition[0]).orderAsc(SearchGridResultDao.Properties.LevensteinDistance).listLazyUncached())
            searchGridResultDao.queryBuilder().where(SearchGridResultDao.Properties.SearchUUID.notEq(searchGridQuery.searchUUID()), WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities()
        }
    }

    @SLMessageHandler
    fun DirGroupsReply(dirGroupsReply: DirGroupsReply) {
        val uuid: UUID = dirGroupsReply.QueryData_Field.QueryID
        val searchGridQuery: SearchGridQuery = this.currentSearchQuery.get()
        if (Objects.equal(searchGridQuery.searchUUID(), uuid) && this.userManager != null && this.searchResultHandler != null) {
            val searchGridResultDao: SearchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao()
            for (DirGroupsReply.QueryReplies queryReplies : dirGroupsReply.QueryReplies_Fields) {
                if (!queryReplies.GroupID.equals(UUIDPool.ZeroUUID)) {
                    val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(queryReplies.GroupName)
                    searchGridResultDao.insert(SearchGridResult((Long) null, uuid, SearchGridQuery.SearchType.Groups.ordinal(), queryReplies.GroupID, stringFromVariableOEM, LevensteinDistance.computeLevensteinDistance(stringFromVariableOEM, searchGridQuery.searchText()), Integer.valueOf(queryReplies.Members)))
                }
            }
            updateSearchResults(searchGridResultDao, searchGridQuery)
        }
    }

    @SLMessageHandler
    fun DirPeopleReply(dirPeopleReply: DirPeopleReply) {
        val searchGridQuery: SearchGridQuery = this.currentSearchQuery.get()
        val uuid: UUID = dirPeopleReply.QueryData_Field.QueryID
        if (Objects.equal(searchGridQuery.searchUUID(), uuid) && this.userManager != null && this.searchResultHandler != null) {
            val searchGridResultDao: SearchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao()
            for (DirPeopleReply.QueryReplies queryReplies : dirPeopleReply.QueryReplies_Fields) {
                val uuid2: UUID = queryReplies.AgentID
                if (uuid2.getLeastSignificantBits() != 0 || uuid2.getMostSignificantBits() != 0) {
                    val str: String = SLMessage.stringFromVariableOEM(queryReplies.FirstName) + " " + SLMessage.stringFromVariableOEM(queryReplies.LastName)
                    searchGridResultDao.insert(SearchGridResult((Long) null, uuid, SearchGridQuery.SearchType.People.ordinal(), uuid2, str, LevensteinDistance.computeLevensteinDistance(str, searchGridQuery.searchText()), 0))
                }
            }
            updateSearchResults(searchGridResultDao, searchGridQuery)
        }
    }

    @SLMessageHandler
    fun DirPlacesReply(dirPlacesReply: DirPlacesReply) {
        val searchGridQuery: SearchGridQuery = this.currentSearchQuery.get()
        for (DirPlacesReply.QueryData queryData : dirPlacesReply.QueryData_Fields) {
            val uuid: UUID = queryData.QueryID
            if (!(!Objects.equal(searchGridQuery.searchUUID(), uuid) || this.userManager == null || this.searchResultHandler == null)) {
                val searchGridResultDao: SearchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao()
                for (DirPlacesReply.QueryReplies queryReplies : dirPlacesReply.QueryReplies_Fields) {
                    if (!queryReplies.ParcelID.equals(UUIDPool.ZeroUUID)) {
                        val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(queryReplies.Name)
                        searchGridResultDao.insert(SearchGridResult((Long) null, uuid, SearchGridQuery.SearchType.Places.ordinal(), queryReplies.ParcelID, stringFromVariableOEM, LevensteinDistance.computeLevensteinDistance(stringFromVariableOEM, searchGridQuery.searchText()), 0))
                    }
                }
                updateSearchResults(searchGridResultDao, searchGridQuery)
            }
        }
    }

    fun HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getSearchManager().searchResults().detachRequestHandler(this.searchRequestHandler)
            this.userManager.parcelInfoData().getRequestSource().detachRequestHandler(this.parcelInfoRequestHandler)
        }
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    fun ParcelInfoReply(parcelInfoReply: ParcelInfoReply) {
        Debug.Printf("ParcelInfo: Got reply for %s", parcelInfoReply.Data_Field.ParcelID)
        if (this.parcelInfoResultHandler != null) {
            this.parcelInfoResultHandler.onResultData(parcelInfoReply.Data_Field.ParcelID, parcelInfoReply)
        }
    }
}
