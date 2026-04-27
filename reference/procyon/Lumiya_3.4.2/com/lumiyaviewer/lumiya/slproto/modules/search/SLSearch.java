// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.search;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.slproto.messages.DirPlacesReply;
import com.lumiyaviewer.lumiya.slproto.messages.DirPeopleReply;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.utils.LevensteinDistance;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.messages.DirGroupsReply;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.WhereCondition;
import com.lumiyaviewer.lumiya.dao.SearchGridResultDao;
import com.lumiyaviewer.lumiya.slproto.messages.DirPlacesQuery;
import com.lumiyaviewer.lumiya.slproto.messages.DirFindQuery;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoRequest;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLSearch extends SLModule
{
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
    private final AtomicReference<SearchGridQuery> currentSearchQuery;
    private final RequestHandler<UUID> parcelInfoRequestHandler;
    private final ResultHandler<UUID, ParcelInfoReply> parcelInfoResultHandler;
    private final RequestHandler<SearchGridQuery> searchRequestHandler;
    private final ResultHandler<SearchGridQuery, LazyList<SearchGridResult>> searchResultHandler;
    private final UserManager userManager;
    
    public SLSearch(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.currentSearchQuery = new AtomicReference<SearchGridQuery>(null);
        this.searchRequestHandler = new AsyncRequestHandler<SearchGridQuery>(this.agentCircuit, new SimpleRequestHandler<SearchGridQuery>() {
            private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues() {
                if (SLSearch$1.-com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues != null) {
                    return SLSearch$1.-com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues;
                }
                int[] -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues = new int[SearchGridQuery.SearchType.values().length];
                while (true) {
                    try {
                        -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues[SearchGridQuery.SearchType.Groups.ordinal()] = 1;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues[SearchGridQuery.SearchType.People.ordinal()] = 2;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues[SearchGridQuery.SearchType.Places.ordinal()] = 3;
                                return -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {
                        continue;
                    }
                    break;
                }
            }
            
            @Override
            public void onRequest(@Nonnull final SearchGridQuery newValue) {
                SLSearch.this.currentSearchQuery.set(newValue);
                switch (-getcom-lumiyaviewer-lumiya-slproto-modules-search-SearchGridQuery$SearchTypeSwitchesValues()[newValue.searchType().ordinal()]) {
                    case 2: {
                        SLSearch.this.SearchPeople(newValue.searchText(), newValue.searchUUID());
                        break;
                    }
                    case 3: {
                        SLSearch.this.SearchPlaces(newValue.searchText(), newValue.searchUUID());
                        break;
                    }
                    case 1: {
                        SLSearch.this.SearchGroups(newValue.searchText(), newValue.searchUUID());
                        break;
                    }
                }
            }
        });
        this.parcelInfoRequestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID parcelID) {
                Debug.Printf("ParcelInfo: Requesting for %s", parcelID);
                final ParcelInfoRequest parcelInfoRequest = new ParcelInfoRequest();
                parcelInfoRequest.AgentData_Field.AgentID = SLSearch.this.circuitInfo.agentID;
                parcelInfoRequest.AgentData_Field.SessionID = SLSearch.this.circuitInfo.sessionID;
                parcelInfoRequest.Data_Field.ParcelID = parcelID;
                parcelInfoRequest.isReliable = true;
                SLSearch.this.SendMessage(parcelInfoRequest);
            }
        }, false, 3, 15000L);
        this.userManager = UserManager.getUserManager(slAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.searchResultHandler = this.userManager.getSearchManager().searchResults().attachRequestHandler(this.searchRequestHandler);
            this.parcelInfoResultHandler = this.userManager.parcelInfoData().getRequestSource().attachRequestHandler(this.parcelInfoRequestHandler);
        }
        else {
            this.searchResultHandler = null;
            this.parcelInfoResultHandler = null;
        }
    }
    
    private void SearchGroups(final String s, final UUID queryID) {
        final DirFindQuery dirFindQuery = new DirFindQuery();
        dirFindQuery.AgentData_Field.AgentID = this.circuitInfo.agentID;
        dirFindQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        dirFindQuery.QueryData_Field.QueryID = queryID;
        dirFindQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(s);
        dirFindQuery.QueryData_Field.QueryFlags = 117440528;
        dirFindQuery.QueryData_Field.QueryStart = 0;
        dirFindQuery.isReliable = true;
        this.SendMessage(dirFindQuery);
    }
    
    private void SearchPeople(final String s, final UUID queryID) {
        final DirFindQuery dirFindQuery = new DirFindQuery();
        dirFindQuery.AgentData_Field.AgentID = this.circuitInfo.agentID;
        dirFindQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        dirFindQuery.QueryData_Field.QueryID = queryID;
        dirFindQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(s);
        dirFindQuery.QueryData_Field.QueryFlags = 117440513;
        dirFindQuery.QueryData_Field.QueryStart = 0;
        dirFindQuery.isReliable = true;
        this.SendMessage(dirFindQuery);
    }
    
    private void SearchPlaces(final String s, final UUID queryID) {
        final DirPlacesQuery dirPlacesQuery = new DirPlacesQuery();
        dirPlacesQuery.AgentData_Field.AgentID = this.circuitInfo.agentID;
        dirPlacesQuery.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        dirPlacesQuery.QueryData_Field.QueryID = queryID;
        dirPlacesQuery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(s);
        dirPlacesQuery.QueryData_Field.QueryFlags = 117440516;
        dirPlacesQuery.QueryData_Field.QueryStart = 0;
        dirPlacesQuery.QueryData_Field.SimName = SLMessage.stringToVariableOEM("");
        dirPlacesQuery.isReliable = true;
        this.SendMessage(dirPlacesQuery);
    }
    
    private void updateSearchResults(final SearchGridResultDao searchGridResultDao, final SearchGridQuery searchGridQuery) {
        if (this.searchResultHandler != null) {
            this.searchResultHandler.onResultData(searchGridQuery, ((AbstractDao<SearchGridResult, K>)searchGridResultDao).queryBuilder().where(SearchGridResultDao.Properties.SearchUUID.eq(searchGridQuery.searchUUID()), new WhereCondition[0]).orderAsc(SearchGridResultDao.Properties.LevensteinDistance).listLazyUncached());
            ((AbstractDao<SearchGridResult, K>)searchGridResultDao).queryBuilder().where(SearchGridResultDao.Properties.SearchUUID.notEq(searchGridQuery.searchUUID()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        }
    }
    
    @SLMessageHandler
    public void DirGroupsReply(final DirGroupsReply dirGroupsReply) {
        final UUID queryID = dirGroupsReply.QueryData_Field.QueryID;
        final SearchGridQuery searchGridQuery = this.currentSearchQuery.get();
        if (Objects.equal(searchGridQuery.searchUUID(), queryID) && this.userManager != null && this.searchResultHandler != null) {
            final SearchGridResultDao searchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao();
            for (final DirGroupsReply.QueryReplies queryReplies : dirGroupsReply.QueryReplies_Fields) {
                if (!queryReplies.GroupID.equals(UUIDPool.ZeroUUID)) {
                    final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(queryReplies.GroupName);
                    ((AbstractDao<SearchGridResult, K>)searchGridResultDao).insert(new SearchGridResult(null, queryID, SearchGridQuery.SearchType.Groups.ordinal(), queryReplies.GroupID, stringFromVariableOEM, LevensteinDistance.computeLevensteinDistance(stringFromVariableOEM, searchGridQuery.searchText()), queryReplies.Members));
                }
            }
            this.updateSearchResults(searchGridResultDao, searchGridQuery);
        }
    }
    
    @SLMessageHandler
    public void DirPeopleReply(final DirPeopleReply dirPeopleReply) {
        final SearchGridQuery searchGridQuery = this.currentSearchQuery.get();
        final UUID queryID = dirPeopleReply.QueryData_Field.QueryID;
        if (Objects.equal(searchGridQuery.searchUUID(), queryID) && this.userManager != null && this.searchResultHandler != null) {
            final SearchGridResultDao searchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao();
            for (final DirPeopleReply.QueryReplies queryReplies : dirPeopleReply.QueryReplies_Fields) {
                final UUID agentID = queryReplies.AgentID;
                if (agentID.getLeastSignificantBits() != 0L || agentID.getMostSignificantBits() != 0L) {
                    final String string = SLMessage.stringFromVariableOEM(queryReplies.FirstName) + " " + SLMessage.stringFromVariableOEM(queryReplies.LastName);
                    ((AbstractDao<SearchGridResult, K>)searchGridResultDao).insert(new SearchGridResult(null, queryID, SearchGridQuery.SearchType.People.ordinal(), agentID, string, LevensteinDistance.computeLevensteinDistance(string, searchGridQuery.searchText()), 0));
                }
            }
            this.updateSearchResults(searchGridResultDao, searchGridQuery);
        }
    }
    
    @SLMessageHandler
    public void DirPlacesReply(final DirPlacesReply dirPlacesReply) {
        final SearchGridQuery searchGridQuery = this.currentSearchQuery.get();
        final Iterator<Object> iterator = dirPlacesReply.QueryData_Fields.iterator();
        while (iterator.hasNext()) {
            final UUID queryID = iterator.next().QueryID;
            if (Objects.equal(searchGridQuery.searchUUID(), queryID) && this.userManager != null && this.searchResultHandler != null) {
                final SearchGridResultDao searchGridResultDao = this.userManager.getSearchManager().getSearchGridResultDao();
                for (final DirPlacesReply.QueryReplies queryReplies : dirPlacesReply.QueryReplies_Fields) {
                    if (!queryReplies.ParcelID.equals(UUIDPool.ZeroUUID)) {
                        final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(queryReplies.Name);
                        ((AbstractDao<SearchGridResult, K>)searchGridResultDao).insert(new SearchGridResult(null, queryID, SearchGridQuery.SearchType.Places.ordinal(), queryReplies.ParcelID, stringFromVariableOEM, LevensteinDistance.computeLevensteinDistance(stringFromVariableOEM, searchGridQuery.searchText()), 0));
                    }
                }
                this.updateSearchResults(searchGridResultDao, searchGridQuery);
            }
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getSearchManager().searchResults().detachRequestHandler(this.searchRequestHandler);
            this.userManager.parcelInfoData().getRequestSource().detachRequestHandler(this.parcelInfoRequestHandler);
        }
        super.HandleCloseCircuit();
    }
    
    @SLMessageHandler
    public void ParcelInfoReply(final ParcelInfoReply parcelInfoReply) {
        Debug.Printf("ParcelInfo: Got reply for %s", parcelInfoReply.Data_Field.ParcelID);
        if (this.parcelInfoResultHandler != null) {
            this.parcelInfoResultHandler.onResultData(parcelInfoReply.Data_Field.ParcelID, parcelInfoReply);
        }
    }
}
