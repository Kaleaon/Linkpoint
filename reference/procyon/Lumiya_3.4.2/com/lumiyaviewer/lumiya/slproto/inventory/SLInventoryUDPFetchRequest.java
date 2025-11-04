// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.FetchInventoryDescendents;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents;
import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

class SLInventoryUDPFetchRequest extends SLInventoryFetchRequest
{
    private final Set<UUID> existingChildren;
    private int receivedCount;
    
    SLInventoryUDPFetchRequest(final SLInventory slInventory, final UUID uuid) throws SLInventory.NoInventoryItemException {
        super(slInventory, uuid);
        this.receivedCount = 0;
        this.existingChildren = new HashSet<UUID>();
    }
    
    boolean HandleInventoryDescendents(final InventoryDescendents p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     4: invokespecial   java/lang/StringBuilder.<init>:()V
        //     7: ldc             "Inventory: UDP fetch: exp count "
        //     9: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    12: aload_1        
        //    13: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.AgentData_Field:Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData;
        //    16: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData.Descendents:I
        //    19: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //    22: ldc             ", recv count "
        //    24: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    27: aload_0        
        //    28: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.receivedCount:I
        //    31: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //    34: ldc             ", "
        //    36: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    39: ldc             " with this: "
        //    41: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    44: aload_0        
        //    45: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.receivedCount:I
        //    48: aload_1        
        //    49: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.FolderData_Fields:Ljava/util/ArrayList;
        //    52: invokevirtual   java/util/ArrayList.size:()I
        //    55: iadd           
        //    56: aload_1        
        //    57: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.ItemData_Fields:Ljava/util/ArrayList;
        //    60: invokevirtual   java/util/ArrayList.size:()I
        //    63: iadd           
        //    64: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //    67: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    70: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    73: aload_1        
        //    74: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.AgentData_Field:Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData;
        //    77: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData.Descendents:I
        //    80: istore_2       
        //    81: iconst_0       
        //    82: istore_3       
        //    83: aload_0        
        //    84: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //    87: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.beginTransaction:()V
        //    90: aload_0        
        //    91: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //    94: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.version:I
        //    97: aload_1        
        //    98: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.AgentData_Field:Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData;
        //   101: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData.Version:I
        //   104: if_icmpeq       132
        //   107: aload_0        
        //   108: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   111: aload_1        
        //   112: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.AgentData_Field:Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData;
        //   115: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData.Version:I
        //   118: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.version:I
        //   121: aload_0        
        //   122: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   125: aload_0        
        //   126: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   129: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.saveEntry:(Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;)V
        //   132: aload_1        
        //   133: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.FolderData_Fields:Ljava/util/ArrayList;
        //   136: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   141: astore          4
        //   143: aload           4
        //   145: invokeinterface java/util/Iterator.hasNext:()Z
        //   150: ifeq            471
        //   153: aload           4
        //   155: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   160: checkcast       Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData;
        //   163: astore          5
        //   165: iload_3        
        //   166: istore          6
        //   168: aload           5
        //   170: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData.ParentID:Ljava/util/UUID;
        //   173: aload_0        
        //   174: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   177: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.uuid:Ljava/util/UUID;
        //   180: invokevirtual   java/util/UUID.equals:(Ljava/lang/Object;)Z
        //   183: ifeq            389
        //   186: aload           5
        //   188: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData.FolderID:Ljava/util/UUID;
        //   191: invokevirtual   java/util/UUID.getLeastSignificantBits:()J
        //   194: lconst_0       
        //   195: lcmp           
        //   196: ifne            219
        //   199: aload           5
        //   201: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData.FolderID:Ljava/util/UUID;
        //   204: invokevirtual   java/util/UUID.getMostSignificantBits:()J
        //   207: lstore          7
        //   209: iload_3        
        //   210: istore          6
        //   212: lload           7
        //   214: lconst_0       
        //   215: lcmp           
        //   216: ifeq            389
        //   219: iload_3        
        //   220: istore          6
        //   222: new             Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   225: astore          9
        //   227: iload_3        
        //   228: istore          6
        //   230: aload           9
        //   232: invokespecial   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.<init>:()V
        //   235: iload_3        
        //   236: istore          6
        //   238: aload           9
        //   240: aload           5
        //   242: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData.FolderID:Ljava/util/UUID;
        //   245: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.uuid:Ljava/util/UUID;
        //   248: iload_3        
        //   249: istore          6
        //   251: aload           9
        //   253: aload_0        
        //   254: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   257: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getId:()J
        //   260: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parent_id:J
        //   263: iload_3        
        //   264: istore          6
        //   266: aload           9
        //   268: aload           5
        //   270: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData.Name:[B
        //   273: invokestatic    com/lumiyaviewer/lumiya/slproto/SLMessage.stringFromVariableOEM:([B)Ljava/lang/String;
        //   276: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.name:Ljava/lang/String;
        //   279: iload_3        
        //   280: istore          6
        //   282: aload           9
        //   284: aload           5
        //   286: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData.Type:I
        //   289: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.typeDefault:I
        //   292: iload_3        
        //   293: istore          6
        //   295: aload           9
        //   297: aload           5
        //   299: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$FolderData.ParentID:Ljava/util/UUID;
        //   302: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parentUUID:Ljava/util/UUID;
        //   305: iload_3        
        //   306: istore          6
        //   308: aload           9
        //   310: aload_1        
        //   311: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.AgentData_Field:Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData;
        //   314: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData.AgentID:Ljava/util/UUID;
        //   317: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.agentUUID:Ljava/util/UUID;
        //   320: iload_3        
        //   321: istore          6
        //   323: aload           9
        //   325: iconst_1       
        //   326: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.isFolder:Z
        //   329: iload_3        
        //   330: istore          6
        //   332: aload           9
        //   334: aload_0        
        //   335: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   338: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.getDatabase:()Landroid/database/sqlite/SQLiteDatabase;
        //   341: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.updateOrInsert:(Landroid/database/sqlite/SQLiteDatabase;)V
        //   344: iload_3        
        //   345: iconst_1       
        //   346: iadd           
        //   347: istore          6
        //   349: iload           6
        //   351: istore_3       
        //   352: iload           6
        //   354: bipush          16
        //   356: if_icmple       368
        //   359: aload_0        
        //   360: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   363: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.yieldIfContendedSafely:()V
        //   366: iconst_0       
        //   367: istore_3       
        //   368: iload_3        
        //   369: istore          6
        //   371: aload_0        
        //   372: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.existingChildren:Ljava/util/Set;
        //   375: aload           9
        //   377: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.uuid:Ljava/util/UUID;
        //   380: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
        //   385: pop            
        //   386: iload_3        
        //   387: istore          6
        //   389: aload_0        
        //   390: aload_0        
        //   391: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.receivedCount:I
        //   394: iconst_1       
        //   395: iadd           
        //   396: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.receivedCount:I
        //   399: iload           6
        //   401: istore_3       
        //   402: goto            143
        //   405: astore_1       
        //   406: aload_1        
        //   407: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   410: aload_0        
        //   411: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   414: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.endTransaction:()V
        //   417: aload_0        
        //   418: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.receivedCount:I
        //   421: iload_2        
        //   422: if_icmplt       1099
        //   425: aload_0        
        //   426: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   429: aload_0        
        //   430: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   433: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getId:()J
        //   436: aload_0        
        //   437: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.existingChildren:Ljava/util/Set;
        //   440: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.retainChildren:(JLjava/util/Set;)V
        //   443: aload_0        
        //   444: iconst_1       
        //   445: iconst_0       
        //   446: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.completeFetch:(ZZ)V
        //   449: iconst_1       
        //   450: ireturn        
        //   451: astore          9
        //   453: aload           9
        //   455: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   458: goto            389
        //   461: astore_1       
        //   462: aload_0        
        //   463: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   466: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.endTransaction:()V
        //   469: aload_1        
        //   470: athrow         
        //   471: aload_1        
        //   472: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.ItemData_Fields:Ljava/util/ArrayList;
        //   475: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   480: astore          4
        //   482: aload           4
        //   484: invokeinterface java/util/Iterator.hasNext:()Z
        //   489: ifeq            1082
        //   492: aload           4
        //   494: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   499: checkcast       Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData;
        //   502: astore          9
        //   504: aload           9
        //   506: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.ItemID:Ljava/util/UUID;
        //   509: invokevirtual   java/util/UUID.getLeastSignificantBits:()J
        //   512: lconst_0       
        //   513: lcmp           
        //   514: ifne            537
        //   517: aload           9
        //   519: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.ItemID:Ljava/util/UUID;
        //   522: invokevirtual   java/util/UUID.getMostSignificantBits:()J
        //   525: lstore          7
        //   527: iload_3        
        //   528: istore          10
        //   530: lload           7
        //   532: lconst_0       
        //   533: lcmp           
        //   534: ifeq            989
        //   537: iload_3        
        //   538: istore          6
        //   540: new             Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   543: astore          5
        //   545: iload_3        
        //   546: istore          6
        //   548: aload           5
        //   550: invokespecial   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.<init>:()V
        //   553: iload_3        
        //   554: istore          6
        //   556: aload           5
        //   558: aload           9
        //   560: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.ItemID:Ljava/util/UUID;
        //   563: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.uuid:Ljava/util/UUID;
        //   566: iload_3        
        //   567: istore          6
        //   569: aload           5
        //   571: aload           9
        //   573: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.Name:[B
        //   576: invokestatic    com/lumiyaviewer/lumiya/slproto/SLMessage.stringFromVariableOEM:([B)Ljava/lang/String;
        //   579: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.name:Ljava/lang/String;
        //   582: iload_3        
        //   583: istore          6
        //   585: aload           5
        //   587: aload           9
        //   589: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.Description:[B
        //   592: invokestatic    com/lumiyaviewer/lumiya/slproto/SLMessage.stringFromVariableUTF:([B)Ljava/lang/String;
        //   595: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.description:Ljava/lang/String;
        //   598: iload_3        
        //   599: istore          6
        //   601: aload           9
        //   603: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.FolderID:Ljava/util/UUID;
        //   606: aload_0        
        //   607: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   610: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.uuid:Ljava/util/UUID;
        //   613: invokevirtual   java/util/UUID.equals:(Ljava/lang/Object;)Z
        //   616: ifeq            1005
        //   619: iload_3        
        //   620: istore          6
        //   622: aload           5
        //   624: aload_0        
        //   625: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   628: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getId:()J
        //   631: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parent_id:J
        //   634: iload_3        
        //   635: istore          6
        //   637: aload           5
        //   639: aload           9
        //   641: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.FolderID:Ljava/util/UUID;
        //   644: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parentUUID:Ljava/util/UUID;
        //   647: iload_3        
        //   648: istore          6
        //   650: aload           5
        //   652: aload_1        
        //   653: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents.AgentData_Field:Lcom/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData;
        //   656: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$AgentData.AgentID:Ljava/util/UUID;
        //   659: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.agentUUID:Ljava/util/UUID;
        //   662: iload_3        
        //   663: istore          6
        //   665: aload           5
        //   667: iconst_0       
        //   668: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.isFolder:Z
        //   671: iload_3        
        //   672: istore          6
        //   674: aload           5
        //   676: aload           9
        //   678: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.Type:I
        //   681: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.assetType:I
        //   684: iload_3        
        //   685: istore          6
        //   687: aload           5
        //   689: aload           9
        //   691: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.AssetID:Ljava/util/UUID;
        //   694: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.assetUUID:Ljava/util/UUID;
        //   697: iload_3        
        //   698: istore          6
        //   700: aload           5
        //   702: aload           9
        //   704: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.InvType:I
        //   707: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.invType:I
        //   710: iload_3        
        //   711: istore          6
        //   713: aload           5
        //   715: aload           9
        //   717: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.Flags:I
        //   720: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.flags:I
        //   723: iload_3        
        //   724: istore          6
        //   726: aload           5
        //   728: aload           9
        //   730: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.CreationDate:I
        //   733: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.creationDate:I
        //   736: iload_3        
        //   737: istore          6
        //   739: aload           5
        //   741: aload           9
        //   743: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.CreatorID:Ljava/util/UUID;
        //   746: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.creatorUUID:Ljava/util/UUID;
        //   749: iload_3        
        //   750: istore          6
        //   752: aload           5
        //   754: aload           9
        //   756: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.GroupID:Ljava/util/UUID;
        //   759: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.groupUUID:Ljava/util/UUID;
        //   762: iload_3        
        //   763: istore          6
        //   765: new             Ljava/util/UUID;
        //   768: astore          11
        //   770: iload_3        
        //   771: istore          6
        //   773: aload           11
        //   775: lconst_0       
        //   776: lconst_0       
        //   777: invokespecial   java/util/UUID.<init>:(JJ)V
        //   780: iload_3        
        //   781: istore          6
        //   783: aload           5
        //   785: aload           11
        //   787: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.lastOwnerUUID:Ljava/util/UUID;
        //   790: iload_3        
        //   791: istore          6
        //   793: aload           5
        //   795: aload           9
        //   797: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.OwnerID:Ljava/util/UUID;
        //   800: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.ownerUUID:Ljava/util/UUID;
        //   803: iload_3        
        //   804: istore          6
        //   806: aload           5
        //   808: aload           9
        //   810: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.GroupOwned:Z
        //   813: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.isGroupOwned:Z
        //   816: iload_3        
        //   817: istore          6
        //   819: aload           5
        //   821: aload           9
        //   823: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.BaseMask:I
        //   826: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.baseMask:I
        //   829: iload_3        
        //   830: istore          6
        //   832: aload           5
        //   834: aload           9
        //   836: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.OwnerMask:I
        //   839: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.ownerMask:I
        //   842: iload_3        
        //   843: istore          6
        //   845: aload           5
        //   847: aload           9
        //   849: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.GroupMask:I
        //   852: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.groupMask:I
        //   855: iload_3        
        //   856: istore          6
        //   858: aload           5
        //   860: aload           9
        //   862: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.EveryoneMask:I
        //   865: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.everyoneMask:I
        //   868: iload_3        
        //   869: istore          6
        //   871: aload           5
        //   873: aload           9
        //   875: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.NextOwnerMask:I
        //   878: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.nextOwnerMask:I
        //   881: iload_3        
        //   882: istore          6
        //   884: aload           5
        //   886: aload           9
        //   888: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.SalePrice:I
        //   891: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.salePrice:I
        //   894: iload_3        
        //   895: istore          6
        //   897: aload           5
        //   899: aload           9
        //   901: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.SaleType:I
        //   904: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.saleType:I
        //   907: iload_3        
        //   908: istore          6
        //   910: aload           5
        //   912: aload_0        
        //   913: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   916: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.getDatabase:()Landroid/database/sqlite/SQLiteDatabase;
        //   919: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.updateOrInsert:(Landroid/database/sqlite/SQLiteDatabase;)V
        //   922: iload_3        
        //   923: iconst_1       
        //   924: iadd           
        //   925: istore          6
        //   927: iload           6
        //   929: istore_3       
        //   930: iload           6
        //   932: bipush          16
        //   934: if_icmple       946
        //   937: aload_0        
        //   938: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //   941: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.yieldIfContendedSafely:()V
        //   944: iconst_0       
        //   945: istore_3       
        //   946: iload_3        
        //   947: istore          10
        //   949: iload_3        
        //   950: istore          6
        //   952: aload           5
        //   954: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parent_id:J
        //   957: aload_0        
        //   958: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.folderEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   961: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getId:()J
        //   964: lcmp           
        //   965: ifne            989
        //   968: iload_3        
        //   969: istore          6
        //   971: aload_0        
        //   972: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.existingChildren:Ljava/util/Set;
        //   975: aload           5
        //   977: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.uuid:Ljava/util/UUID;
        //   980: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
        //   985: pop            
        //   986: iload_3        
        //   987: istore          10
        //   989: aload_0        
        //   990: aload_0        
        //   991: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.receivedCount:I
        //   994: iconst_1       
        //   995: iadd           
        //   996: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.receivedCount:I
        //   999: iload           10
        //  1001: istore_3       
        //  1002: goto            482
        //  1005: iload_3        
        //  1006: istore          6
        //  1008: aload_0        
        //  1009: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //  1012: aload           9
        //  1014: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.FolderID:Ljava/util/UUID;
        //  1017: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.findEntry:(Ljava/util/UUID;)Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //  1020: astore          11
        //  1022: aload           11
        //  1024: ifnull          1070
        //  1027: iload_3        
        //  1028: istore          6
        //  1030: aload           5
        //  1032: aload           11
        //  1034: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getId:()J
        //  1037: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parent_id:J
        //  1040: iload_3        
        //  1041: istore          6
        //  1043: aload           5
        //  1045: aload           9
        //  1047: getfield        com/lumiyaviewer/lumiya/slproto/messages/InventoryDescendents$ItemData.FolderID:Ljava/util/UUID;
        //  1050: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parentUUID:Ljava/util/UUID;
        //  1053: goto            647
        //  1056: astore          9
        //  1058: aload           9
        //  1060: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //  1063: iload           6
        //  1065: istore          10
        //  1067: goto            989
        //  1070: iload_3        
        //  1071: istore          6
        //  1073: aload           5
        //  1075: lconst_0       
        //  1076: putfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.parent_id:J
        //  1079: goto            1040
        //  1082: aload_0        
        //  1083: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //  1086: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.setTransactionSuccessful:()V
        //  1089: aload_0        
        //  1090: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryUDPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
        //  1093: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.endTransaction:()V
        //  1096: goto            417
        //  1099: iconst_0       
        //  1100: ireturn        
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                           
        //  -----  -----  -----  -----  ---------------------------------------------------------------
        //  90     132    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  90     132    461    471    Any
        //  132    143    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  132    143    461    471    Any
        //  143    165    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  143    165    461    471    Any
        //  168    209    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  168    209    461    471    Any
        //  222    227    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  222    227    461    471    Any
        //  230    235    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  230    235    461    471    Any
        //  238    248    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  238    248    461    471    Any
        //  251    263    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  251    263    461    471    Any
        //  266    279    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  266    279    461    471    Any
        //  282    292    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  282    292    461    471    Any
        //  295    305    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  295    305    461    471    Any
        //  308    320    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  308    320    461    471    Any
        //  323    329    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  323    329    461    471    Any
        //  332    344    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  332    344    461    471    Any
        //  359    366    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  359    366    461    471    Any
        //  371    386    451    461    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  371    386    461    471    Any
        //  389    399    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  389    399    461    471    Any
        //  406    410    461    471    Any
        //  453    458    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  453    458    461    471    Any
        //  471    482    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  471    482    461    471    Any
        //  482    527    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  482    527    461    471    Any
        //  540    545    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  540    545    461    471    Any
        //  548    553    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  548    553    461    471    Any
        //  556    566    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  556    566    461    471    Any
        //  569    582    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  569    582    461    471    Any
        //  585    598    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  585    598    461    471    Any
        //  601    619    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  601    619    461    471    Any
        //  622    634    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  622    634    461    471    Any
        //  637    647    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  637    647    461    471    Any
        //  650    662    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  650    662    461    471    Any
        //  665    671    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  665    671    461    471    Any
        //  674    684    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  674    684    461    471    Any
        //  687    697    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  687    697    461    471    Any
        //  700    710    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  700    710    461    471    Any
        //  713    723    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  713    723    461    471    Any
        //  726    736    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  726    736    461    471    Any
        //  739    749    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  739    749    461    471    Any
        //  752    762    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  752    762    461    471    Any
        //  765    770    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  765    770    461    471    Any
        //  773    780    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  773    780    461    471    Any
        //  783    790    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  783    790    461    471    Any
        //  793    803    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  793    803    461    471    Any
        //  806    816    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  806    816    461    471    Any
        //  819    829    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  819    829    461    471    Any
        //  832    842    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  832    842    461    471    Any
        //  845    855    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  845    855    461    471    Any
        //  858    868    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  858    868    461    471    Any
        //  871    881    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  871    881    461    471    Any
        //  884    894    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  884    894    461    471    Any
        //  897    907    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  897    907    461    471    Any
        //  910    922    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  910    922    461    471    Any
        //  937    944    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  937    944    461    471    Any
        //  952    968    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  952    968    461    471    Any
        //  971    986    1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  971    986    461    471    Any
        //  989    999    405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  989    999    461    471    Any
        //  1008   1022   1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  1008   1022   461    471    Any
        //  1030   1040   1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  1030   1040   461    471    Any
        //  1043   1053   1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  1043   1053   461    471    Any
        //  1058   1063   405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  1058   1063   461    471    Any
        //  1073   1079   1056   1070   Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  1073   1079   461    471    Any
        //  1082   1089   405    417    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
        //  1082   1089   461    471    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0368:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public void cancel() {
    }
    
    @Override
    public void start() {
        Debug.Log("Inventory: UDP fetching folder " + this.folderUUID.toString());
        final FetchInventoryDescendents fetchInventoryDescendents = new FetchInventoryDescendents();
        fetchInventoryDescendents.AgentData_Field.AgentID = this.inventory.getCircuitInfo().agentID;
        fetchInventoryDescendents.AgentData_Field.SessionID = this.inventory.getCircuitInfo().sessionID;
        fetchInventoryDescendents.InventoryData_Field.FolderID = this.folderUUID;
        fetchInventoryDescendents.InventoryData_Field.OwnerID = this.inventory.getCircuitInfo().agentID;
        fetchInventoryDescendents.InventoryData_Field.FetchFolders = true;
        fetchInventoryDescendents.InventoryData_Field.FetchItems = true;
        fetchInventoryDescendents.isReliable = true;
        this.inventory.SendMessage(fetchInventoryDescendents);
    }
}
