package com.lumiyaviewer.lumiya.slproto.modules.search;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import com.lumiyaviewer.lumiya.dao.SearchGridResultDao;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.DirFindQuery;
import com.lumiyaviewer.lumiya.slproto.messages.DirGroupsReply;
import com.lumiyaviewer.lumiya.slproto.messages.DirPeopleReply;
import com.lumiyaviewer.lumiya.slproto.messages.DirPlacesQuery;
import com.lumiyaviewer.lumiya.slproto.messages.DirPlacesReply;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoRequest;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.LevensteinDistance;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.WhereCondition;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class SLSearch extends SLModule {
    private static final int DFQ_ADULT_SIMS_ONLY = 134217728;
    private static final int DFQ_AGENT_OWNED = 64;
    private static final int DFQ_AREA_SORT = 262144;
    private static final int DFQ_AUCTION = 512;
    private static final int DFQ_DATE_EVENTS = 32;
    private static final int DFQ_DWELL_SORT = 1024;
    private static final int DFQ_EVENTS = 8;
    private static final int DFQ_FILTER_MATURE = 4194304;
    private static final int DFQ_FOR_SALE = 128;
    private static final int DFQ_GROUPS = 16;
    private static final int DFQ_GROUP_OWNED = 256;
    private static final int DFQ_INC_ADULT = 67108864;
    private static final int DFQ_INC_MATURE = 33554432;
    private static final int DFQ_INC_NEW_VIEWER = 117440512;
    private static final int DFQ_INC_PG = 16777216;
    private static final int DFQ_LIMIT_BY_AREA = 2097152;
    private static final int DFQ_LIMIT_BY_PRICE = 1048576;
    private static final int DFQ_MATURE_SIMS_ONLY = 16384;
    private static final int DFQ_NAME_SORT = 524288;
    private static final int DFQ_ONLINE = 2;
    private static final int DFQ_PEOPLE = 1;
    private static final int DFQ_PER_METER_SORT = 131072;
    private static final int DFQ_PG_EVENTS_ONLY = 8192;
    private static final int DFQ_PG_PARCELS_ONLY = 8388608;
    private static final int DFQ_PG_SIMS_ONLY = 2048;
    private static final int DFQ_PICTURES_ONLY = 4096;
    private static final int DFQ_PLACES = 4;
    private static final int DFQ_PRICE_SORT = 65536;
    private static final int DFQ_SORT_ASC = 32768;
    /* access modifiers changed from: private */
    public final AtomicReference<SearchGridQuery> currentSearchQuery = new AtomicReference<>((Object) null);
    private final RequestHandler<UUID> parcelInfoRequestHandler = new AsyncLimitsRequestHandler(this.agentCircuit, new SimpleRequestHandler<UUID>() {
        public void onRequest(@Nonnull UUID uuid) {
            Debug.Printf("ParcelInfo: Requesting for %s", uuid);
            ParcelInfoRequest parcelInfoRequest = new ParcelInfoRequest();
            parcelInfoRequest.AgentData_Field.AgentID = SLSearch.this.circuitInfo.agentID;
            parcelInfoRequest.AgentData_Field.SessionID = SLSearch.this.circuitInfo.sessionID;
            parcelInfoRequest.Data_Field.ParcelID = uuid;
            parcelInfoRequest.isReliable = true;
            SLSearch.this.SendMessage(parcelInfoRequest);
        }
    }, false, 3, 15000);
    private final ResultHandler<UUID, ParcelInfoReply> parcelInfoResultHandler;
    private final RequestHandler<SearchGridQuery> searchRequestHandler = new AsyncRequestHandler(this.agentCircuit, new SimpleRequestHandler<SearchGridQuery>() {

        /* renamed from: -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
        private static final /* synthetic */ int[] f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$modules$search$SearchGridQuery$SearchType;

        /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues  reason: not valid java name */
        private static /* synthetic */ int[] m238getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues() {
            if (f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues != null) {
                return f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues;
            }
            int[] iArr = new int[SearchGridQuery.SearchType.values().length];
            try {
                iArr[SearchGridQuery.SearchType.Groups.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[SearchGridQuery.SearchType.People.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[SearchGridQuery.SearchType.Places.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            f129comlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues = iArr;
            return iArr;
        }

        public void onRequest(@Nonnull SearchGridQuery searchGridQuery) {
            SLSearch.this.currentSearchQuery.set(searchGridQuery);
            switch (m238getcomlumiyaviewerlumiyaslprotomodulessearchSearchGridQuery$SearchTypeSwitchesValues()[searchGridQuery.searchType().ordinal()]) {
                case 1:
                    SLSearch.this.SearchGroups(searchGridQuery.searchText(), searchGridQuery.searchUUID());
                    return;
                case 2:
                    SLSearch.this.SearchPeople(searchGridQuery.searchText(), searchGridQuery.searchUUID());
                    return;
                case 3:
                    SLSearch.this.SearchPlaces(searchGridQuery.searchText(), searchGridQuery.searchUUID());
                    return;
                default:
                    return;
            }
        }
    private final ResultHandler<SearchGridQuery, LazyList<SearchGridResult>> searchResultHandler;
    private final UserManager userManager;

    public SLSearch(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.searchResultHandler = this.userManager.getSearchManager().searchResults().attachRequestHandler(this.searchRequestHandler);
            this.parcelInfoResultHandler = this.userManager.parcelInfoData().getRequestSource().attachRequestHandler(this.parcelInfoRequestHandler);
            return;
        }
        this.searchResultHandler = null;
        this.parcelInfoResultHandler = null;
    }

    /* access modifiers changed from: private */
    public void SearchGroups(String str, UUID uuid) {
        DirFindQuery dirFindQuery = new DirFindQuery();
        dirFindQuery.AgentData_Field.AgentID = this.circuitInfo.agentID;
        dirFindQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        dirFindQuery.QueryData_Field.QueryID = uuid;
        dirFindQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(str);
        dirFindQuery.QueryData_Field.QueryFlags = 117440528;
        dirFindQuery.QueryData_Field.QueryStart = 0;
        dirFindQuery.isReliable = true;
        SendMessage(dirFindQuery);
    }

    /* access modifiers changed from: private */
    public void SearchPeople(String str, UUID uuid) {
        DirFindQuery dirFindQuery = new DirFindQuery();
        dirFindQuery.AgentData_Field.AgentID = this.circuitInfo.agentID;
        dirFindQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        dirFindQuery.QueryData_Field.QueryID = uuid;
        dirFindQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(str);
        dirFindQuery.QueryData_Field.QueryFlags = 117440513;
        dirFindQuery.QueryData_Field.QueryStart = 0;
        dirFindQuery.isReliable = true;
        SendMessage(dirFindQuery);
    }

    /* access modifiers changed from: private */
    public void SearchPlaces(String str, UUID uuid) {
        DirPlacesQuery dirPlacesQuery = new DirPlacesQuery();
        dirPlacesQuery.AgentData_Field.AgentID = this.circuitInfo.agentID;
        dirPlacesQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        dirPlacesQuery.QueryData_Field.QueryID = uuid;
        dirPlacesQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(str);
        dirPlacesQuery.QueryData_Field.QueryFlags = 117440516;
        dirPlacesQuery.QueryData_Field.QueryStart = 0;
        dirPlacesQuery.QueryData_Field.SimName = SLMessage.stringToVariableOEM("");
        dirPlacesQuery.isReliable = true;
        SendMessage(dirPlacesQuery);
    }

    private void updateSearchResults(SearchGridResultDao searchGridResultDao, SearchGridQuery searchGridQuery) {
        if (this.searchResultHandler != null) {
            this.searchResultHandler.onResultData(searchGridQuery, searchGridResultDao.queryBuilder().where(SearchGridResultDao.Properties.SearchUUID.eq(searchGridQuery.searchUUID()), new WhereCondition[0]).orderAsc(SearchGridResultDao.Properties.LevensteinDistance).listLazyUncached());
            searchGridResultDao.queryBuilder().where(SearchGridResultDao.Properties.SearchUUID.notEq(searchGridQuery.searchUUID()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        }
    }

    @SLMessageHandler
    public void DirGroupsReply(DirGroupsReply dirGroupsReply) {
        UUID uuid = dirGroupsReply.QueryData_Field.QueryID;
        SearchGridQuery searchGridQuery = this.currentSearchQuery.get();
        if (Objects.equal(searchGridQuery.searchUUID(), uuid) && this.userManager != null && this.searchResultHandler != null) {
            SearchGridResultDao searchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao();
            for (DirGroupsReply.QueryReplies queryReplies : dirGroupsReply.QueryReplies_Fields) {
                if (!queryReplies.GroupID.equals(UUIDPool.ZeroUUID)) {
                    String stringFromVariableOEM = SLMessage.stringFromVariableOEM(queryReplies.GroupName);
                    searchGridResultDao.insert(new SearchGridResult((Long) null, uuid, SearchGridQuery.SearchType.Groups.ordinal(), queryReplies.GroupID, stringFromVariableOEM, LevensteinDistance.computeLevensteinDistance(stringFromVariableOEM, searchGridQuery.searchText()), Integer.valueOf(queryReplies.Members)));
                }
            }
            updateSearchResults(searchGridResultDao, searchGridQuery);
        }
    }

    @SLMessageHandler
    public void DirPeopleReply(DirPeopleReply dirPeopleReply) {
        SearchGridQuery searchGridQuery = this.currentSearchQuery.get();
        UUID uuid = dirPeopleReply.QueryData_Field.QueryID;
        if (Objects.equal(searchGridQuery.searchUUID(), uuid) && this.userManager != null && this.searchResultHandler != null) {
            SearchGridResultDao searchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao();
            for (DirPeopleReply.QueryReplies queryReplies : dirPeopleReply.QueryReplies_Fields) {
                UUID uuid2 = queryReplies.AgentID;
                if (uuid2.getLeastSignificantBits() != 0 || uuid2.getMostSignificantBits() != 0) {
                    String str = SLMessage.stringFromVariableOEM(queryReplies.FirstName) + " " + SLMessage.stringFromVariableOEM(queryReplies.LastName);
                    searchGridResultDao.insert(new SearchGridResult((Long) null, uuid, SearchGridQuery.SearchType.People.ordinal(), uuid2, str, LevensteinDistance.computeLevensteinDistance(str, searchGridQuery.searchText()), 0));
                }
            }
            updateSearchResults(searchGridResultDao, searchGridQuery);
        }
    }

    @SLMessageHandler
    public void DirPlacesReply(DirPlacesReply dirPlacesReply) {
        SearchGridQuery searchGridQuery = this.currentSearchQuery.get();
        for (DirPlacesReply.QueryData queryData : dirPlacesReply.QueryData_Fields) {
            UUID uuid = queryData.QueryID;
            if (!(!Objects.equal(searchGridQuery.searchUUID(), uuid) || this.userManager == null || this.searchResultHandler == null)) {
                SearchGridResultDao searchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao();
                for (DirPlacesReply.QueryReplies queryReplies : dirPlacesReply.QueryReplies_Fields) {
                    if (!queryReplies.ParcelID.equals(UUIDPool.ZeroUUID)) {
                        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(queryReplies.Name);
                        searchGridResultDao.insert(new SearchGridResult((Long) null, uuid, SearchGridQuery.SearchType.Places.ordinal(), queryReplies.ParcelID, stringFromVariableOEM, LevensteinDistance.computeLevensteinDistance(stringFromVariableOEM, searchGridQuery.searchText()), 0));
                    }
                }
                updateSearchResults(searchGridResultDao, searchGridQuery);
            }
        }
    }

    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getSearchManager().searchResults().detachRequestHandler(this.searchRequestHandler);
            this.userManager.parcelInfoData().getRequestSource().detachRequestHandler(this.parcelInfoRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    @SLMessageHandler
    public void ParcelInfoReply(ParcelInfoReply parcelInfoReply) {
        Debug.Printf("ParcelInfo: Got reply for %s", parcelInfoReply.Data_Field.ParcelID);
        if (this.parcelInfoResultHandler != null) {
            this.parcelInfoResultHandler.onResultData(parcelInfoReply.Data_Field.ParcelID, parcelInfoReply);
        }
    }
}
