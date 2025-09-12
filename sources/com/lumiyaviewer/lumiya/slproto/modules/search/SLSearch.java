// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.search;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import com.lumiyaviewer.lumiya.dao.SearchGridResultDao;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.DirFindQuery;
import com.lumiyaviewer.lumiya.slproto.messages.DirGroupsReply;
import com.lumiyaviewer.lumiya.slproto.messages.DirPeopleReply;
import com.lumiyaviewer.lumiya.slproto.messages.DirPlacesQuery;
import com.lumiyaviewer.lumiya.slproto.messages.DirPlacesReply;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoRequest;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.SearchManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.LevensteinDistance;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.search:
//            SearchGridQuery

public class SLSearch extends SLModule
{

    private static final int DFQ_ADULT_SIMS_ONLY = 0x8000000;
    private static final int DFQ_AGENT_OWNED = 64;
    private static final int DFQ_AREA_SORT = 0x40000;
    private static final int DFQ_AUCTION = 512;
    private static final int DFQ_DATE_EVENTS = 32;
    private static final int DFQ_DWELL_SORT = 1024;
    private static final int DFQ_EVENTS = 8;
    private static final int DFQ_FILTER_MATURE = 0x400000;
    private static final int DFQ_FOR_SALE = 128;
    private static final int DFQ_GROUPS = 16;
    private static final int DFQ_GROUP_OWNED = 256;
    private static final int DFQ_INC_ADULT = 0x4000000;
    private static final int DFQ_INC_MATURE = 0x2000000;
    private static final int DFQ_INC_NEW_VIEWER = 0x7000000;
    private static final int DFQ_INC_PG = 0x1000000;
    private static final int DFQ_LIMIT_BY_AREA = 0x200000;
    private static final int DFQ_LIMIT_BY_PRICE = 0x100000;
    private static final int DFQ_MATURE_SIMS_ONLY = 16384;
    private static final int DFQ_NAME_SORT = 0x80000;
    private static final int DFQ_ONLINE = 2;
    private static final int DFQ_PEOPLE = 1;
    private static final int DFQ_PER_METER_SORT = 0x20000;
    private static final int DFQ_PG_EVENTS_ONLY = 8192;
    private static final int DFQ_PG_PARCELS_ONLY = 0x800000;
    private static final int DFQ_PG_SIMS_ONLY = 2048;
    private static final int DFQ_PICTURES_ONLY = 4096;
    private static final int DFQ_PLACES = 4;
    private static final int DFQ_PRICE_SORT = 0x10000;
    private static final int DFQ_SORT_ASC = 32768;
    private final AtomicReference currentSearchQuery = new AtomicReference(null);
    private final RequestHandler parcelInfoRequestHandler;
    private final ResultHandler parcelInfoResultHandler;
    private final RequestHandler searchRequestHandler;
    private final ResultHandler searchResultHandler;
    private final UserManager userManager;

    static SLCircuitInfo _2D_get0(SLSearch slsearch)
    {
        return slsearch.circuitInfo;
    }

    static AtomicReference _2D_get1(SLSearch slsearch)
    {
        return slsearch.currentSearchQuery;
    }

    static void _2D_wrap0(SLSearch slsearch, String s, UUID uuid)
    {
        slsearch.SearchGroups(s, uuid);
    }

    static void _2D_wrap1(SLSearch slsearch, String s, UUID uuid)
    {
        slsearch.SearchPeople(s, uuid);
    }

    static void _2D_wrap2(SLSearch slsearch, String s, UUID uuid)
    {
        slsearch.SearchPlaces(s, uuid);
    }

    public SLSearch(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        searchRequestHandler = new AsyncRequestHandler(agentCircuit, new SimpleRequestHandler() {

            private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues[];
            final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$modules$search$SearchGridQuery$SearchType[];
            final SLSearch this$0;

            private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues()
            {
                if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues != null)
                {
                    return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues;
                }
                int ai[] = new int[SearchGridQuery.SearchType.values().length];
                try
                {
                    ai[SearchGridQuery.SearchType.Groups.ordinal()] = 1;
                }
                catch (NoSuchFieldError nosuchfielderror2) { }
                try
                {
                    ai[SearchGridQuery.SearchType.People.ordinal()] = 2;
                }
                catch (NoSuchFieldError nosuchfielderror1) { }
                try
                {
                    ai[SearchGridQuery.SearchType.Places.ordinal()] = 3;
                }
                catch (NoSuchFieldError nosuchfielderror) { }
                _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues = ai;
                return ai;
            }

            public void onRequest(SearchGridQuery searchgridquery)
            {
                SLSearch._2D_get1(SLSearch.this).set(searchgridquery);
                switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_search_2D_SearchGridQuery$SearchTypeSwitchesValues()[searchgridquery.searchType().ordinal()])
                {
                default:
                    return;

                case 2: // '\002'
                    SLSearch._2D_wrap1(SLSearch.this, searchgridquery.searchText(), searchgridquery.searchUUID());
                    return;

                case 3: // '\003'
                    SLSearch._2D_wrap2(SLSearch.this, searchgridquery.searchText(), searchgridquery.searchUUID());
                    return;

                case 1: // '\001'
                    SLSearch._2D_wrap0(SLSearch.this, searchgridquery.searchText(), searchgridquery.searchUUID());
                    return;
                }
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((SearchGridQuery)obj);
            }

            
            {
                this$0 = SLSearch.this;
                super();
            }
        });
        parcelInfoRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLSearch this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                Debug.Printf("ParcelInfo: Requesting for %s", new Object[] {
                    uuid
                });
                ParcelInfoRequest parcelinforequest = new ParcelInfoRequest();
                parcelinforequest.AgentData_Field.AgentID = SLSearch._2D_get0(SLSearch.this).agentID;
                parcelinforequest.AgentData_Field.SessionID = SLSearch._2D_get0(SLSearch.this).sessionID;
                parcelinforequest.Data_Field.ParcelID = uuid;
                parcelinforequest.isReliable = true;
                SendMessage(parcelinforequest);
            }

            
            {
                this$0 = SLSearch.this;
                super();
            }
        }, false, 3, 15000L);
        userManager = UserManager.getUserManager(slagentcircuit.getAgentUUID());
        if (userManager != null)
        {
            searchResultHandler = userManager.getSearchManager().searchResults().attachRequestHandler(searchRequestHandler);
            parcelInfoResultHandler = userManager.parcelInfoData().getRequestSource().attachRequestHandler(parcelInfoRequestHandler);
            return;
        } else
        {
            searchResultHandler = null;
            parcelInfoResultHandler = null;
            return;
        }
    }

    private void SearchGroups(String s, UUID uuid)
    {
        DirFindQuery dirfindquery = new DirFindQuery();
        dirfindquery.AgentData_Field.AgentID = circuitInfo.agentID;
        dirfindquery.AgentData_Field.SessionID = circuitInfo.sessionID;
        dirfindquery.QueryData_Field.QueryID = uuid;
        dirfindquery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(s);
        dirfindquery.QueryData_Field.QueryFlags = 0x7000010;
        dirfindquery.QueryData_Field.QueryStart = 0;
        dirfindquery.isReliable = true;
        SendMessage(dirfindquery);
    }

    private void SearchPeople(String s, UUID uuid)
    {
        DirFindQuery dirfindquery = new DirFindQuery();
        dirfindquery.AgentData_Field.AgentID = circuitInfo.agentID;
        dirfindquery.AgentData_Field.SessionID = circuitInfo.sessionID;
        dirfindquery.QueryData_Field.QueryID = uuid;
        dirfindquery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(s);
        dirfindquery.QueryData_Field.QueryFlags = 0x7000001;
        dirfindquery.QueryData_Field.QueryStart = 0;
        dirfindquery.isReliable = true;
        SendMessage(dirfindquery);
    }

    private void SearchPlaces(String s, UUID uuid)
    {
        DirPlacesQuery dirplacesquery = new DirPlacesQuery();
        dirplacesquery.AgentData_Field.AgentID = circuitInfo.agentID;
        dirplacesquery.AgentData_Field.SessionID = circuitInfo.sessionID;
        dirplacesquery.QueryData_Field.QueryID = uuid;
        dirplacesquery.QueryData_Field.QueryText = SLMessage.stringToVariableUTF(s);
        dirplacesquery.QueryData_Field.QueryFlags = 0x7000004;
        dirplacesquery.QueryData_Field.QueryStart = 0;
        dirplacesquery.QueryData_Field.SimName = SLMessage.stringToVariableOEM("");
        dirplacesquery.isReliable = true;
        SendMessage(dirplacesquery);
    }

    private void updateSearchResults(SearchGridResultDao searchgridresultdao, SearchGridQuery searchgridquery)
    {
        if (searchResultHandler != null)
        {
            de.greenrobot.dao.query.LazyList lazylist = searchgridresultdao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.SearchGridResultDao.Properties.SearchUUID.eq(searchgridquery.searchUUID()), new WhereCondition[0]).orderAsc(new Property[] {
                com.lumiyaviewer.lumiya.dao.SearchGridResultDao.Properties.LevensteinDistance
            }).listLazyUncached();
            searchResultHandler.onResultData(searchgridquery, lazylist);
            searchgridresultdao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.SearchGridResultDao.Properties.SearchUUID.notEq(searchgridquery.searchUUID()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        }
    }

    public void DirGroupsReply(DirGroupsReply dirgroupsreply)
    {
        UUID uuid = dirgroupsreply.QueryData_Field.QueryID;
        SearchGridQuery searchgridquery = (SearchGridQuery)currentSearchQuery.get();
        if (Objects.equal(searchgridquery.searchUUID(), uuid) && userManager != null && searchResultHandler != null)
        {
            SearchGridResultDao searchgridresultdao = userManager.getSearchManager().getSearchGridResultDao();
            dirgroupsreply = dirgroupsreply.QueryReplies_Fields.iterator();
            do
            {
                if (!dirgroupsreply.hasNext())
                {
                    break;
                }
                com.lumiyaviewer.lumiya.slproto.messages.DirGroupsReply.QueryReplies queryreplies = (com.lumiyaviewer.lumiya.slproto.messages.DirGroupsReply.QueryReplies)dirgroupsreply.next();
                if (!queryreplies.GroupID.equals(UUIDPool.ZeroUUID))
                {
                    String s = SLMessage.stringFromVariableOEM(queryreplies.GroupName);
                    int i = LevensteinDistance.computeLevensteinDistance(s, searchgridquery.searchText());
                    searchgridresultdao.insert(new SearchGridResult(null, uuid, SearchGridQuery.SearchType.Groups.ordinal(), queryreplies.GroupID, s, i, Integer.valueOf(queryreplies.Members)));
                }
            } while (true);
            updateSearchResults(searchgridresultdao, searchgridquery);
        }
    }

    public void DirPeopleReply(DirPeopleReply dirpeoplereply)
    {
        SearchGridQuery searchgridquery = (SearchGridQuery)currentSearchQuery.get();
        UUID uuid = dirpeoplereply.QueryData_Field.QueryID;
        if (Objects.equal(searchgridquery.searchUUID(), uuid) && userManager != null && searchResultHandler != null)
        {
            SearchGridResultDao searchgridresultdao = userManager.getSearchManager().getSearchGridResultDao();
            dirpeoplereply = dirpeoplereply.QueryReplies_Fields.iterator();
            do
            {
                if (!dirpeoplereply.hasNext())
                {
                    break;
                }
                Object obj = (com.lumiyaviewer.lumiya.slproto.messages.DirPeopleReply.QueryReplies)dirpeoplereply.next();
                UUID uuid1 = ((com.lumiyaviewer.lumiya.slproto.messages.DirPeopleReply.QueryReplies) (obj)).AgentID;
                if (uuid1.getLeastSignificantBits() != 0L || uuid1.getMostSignificantBits() != 0L)
                {
                    obj = (new StringBuilder()).append(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.DirPeopleReply.QueryReplies) (obj)).FirstName)).append(" ").append(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.DirPeopleReply.QueryReplies) (obj)).LastName)).toString();
                    int i = LevensteinDistance.computeLevensteinDistance(((CharSequence) (obj)), searchgridquery.searchText());
                    searchgridresultdao.insert(new SearchGridResult(null, uuid, SearchGridQuery.SearchType.People.ordinal(), uuid1, ((String) (obj)), i, Integer.valueOf(0)));
                }
            } while (true);
            updateSearchResults(searchgridresultdao, searchgridquery);
        }
    }

    public void DirPlacesReply(DirPlacesReply dirplacesreply)
    {
        SearchGridQuery searchgridquery = (SearchGridQuery)currentSearchQuery.get();
        Iterator iterator = dirplacesreply.QueryData_Fields.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.messages.DirPlacesReply.QueryData)iterator.next()).QueryID;
            if (Objects.equal(searchgridquery.searchUUID(), uuid) && userManager != null && searchResultHandler != null)
            {
                SearchGridResultDao searchgridresultdao = userManager.getSearchManager().getSearchGridResultDao();
                Iterator iterator1 = dirplacesreply.QueryReplies_Fields.iterator();
                do
                {
                    if (!iterator1.hasNext())
                    {
                        break;
                    }
                    com.lumiyaviewer.lumiya.slproto.messages.DirPlacesReply.QueryReplies queryreplies = (com.lumiyaviewer.lumiya.slproto.messages.DirPlacesReply.QueryReplies)iterator1.next();
                    if (!queryreplies.ParcelID.equals(UUIDPool.ZeroUUID))
                    {
                        String s = SLMessage.stringFromVariableOEM(queryreplies.Name);
                        int i = LevensteinDistance.computeLevensteinDistance(s, searchgridquery.searchText());
                        searchgridresultdao.insert(new SearchGridResult(null, uuid, SearchGridQuery.SearchType.Places.ordinal(), queryreplies.ParcelID, s, i, Integer.valueOf(0)));
                    }
                } while (true);
                updateSearchResults(searchgridresultdao, searchgridquery);
            }
        } while (true);
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.getSearchManager().searchResults().detachRequestHandler(searchRequestHandler);
            userManager.parcelInfoData().getRequestSource().detachRequestHandler(parcelInfoRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    public void ParcelInfoReply(ParcelInfoReply parcelinforeply)
    {
        Debug.Printf("ParcelInfo: Got reply for %s", new Object[] {
            parcelinforeply.Data_Field.ParcelID
        });
        if (parcelInfoResultHandler != null)
        {
            parcelInfoResultHandler.onResultData(parcelinforeply.Data_Field.ParcelID, parcelinforeply);
        }
    }
}
