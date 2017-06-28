package CoroUtil.quest.quests;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import CoroUtil.quest.EnumQuestState;
import CoroUtil.util.CoroUtilEntity;
import CoroUtil.util.CoroUtilNBT;
import cpw.mods.fml.common.eventhandler.Event;

public class BreakBlockQuest extends ActiveQuest {
	
	//quest supports breaking a block at a coordinate AND/OR breaking a specific block (x number of times), determines rules based on null or not null values
	
	//note, count is for amount of blocks needed to break, not count in inventory
	
	//configurations
	public ChunkCoordinates blockCoords;
	public String blockType;
	public int blockCountNeeded = -1;
	
	//progression
	public int blockCountCurrent = 0;

	public BreakBlockQuest() {
		questType = "breakBlock";
	}
	
	public void initCustomData(ChunkCoordinates parCoords, Block parBlock) {
		super.initCustomData();

		blockCoords = parCoords;
		blockType = Block.blockRegistry.getNameForObject(parBlock);
		
	}

	@Override
	public void tick() {
		super.tick();
		
		if (curState == EnumQuestState.ASSIGNED) {
			
			if (blockCountNeeded != -1) {
				if (blockCountCurrent >= blockCountNeeded) {
					if (returnToQuestGiver) {
						setState(EnumQuestState.CONCLUDING);
					} else {
						eventComplete();
					}
				}
			}
		} else if (curState == EnumQuestState.CONCLUDING) {
			//logic that determines they have talked to the quest giver to complete the quest, should this be here or in the koa?
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if (event instanceof BreakEvent) {
			handleEvent((BreakEvent)event);
		}
	}
	
	public void handleEvent(BreakEvent event) {
		cpw.mods.fml.common.FMLLog.info("EVENT!: " + event.getPlayer() + " - " + event.x + " - " + event.y + " - " + event.z);
		if (event.getPlayer() == null || !CoroUtilEntity.getName(event.getPlayer()).equals(playerQuests.playerName)) {
			return;
		}
		if (getBlock() != null) {
			if (getBlock() != event.block) {
				return;
			}
		}
		if (blockCoords != null) {
			if (blockCoords.posX != event.x || blockCoords.posY != event.y || blockCoords.posZ != event.z) {
				return;
			}
		}
		if (blockCountNeeded != -1) {
			blockCountCurrent++;
		} else {
			eventComplete();
		}
		saveAndSync();
	}
	
	public Block getBlock() {
		return (Block)Block.blockRegistry.getObject(blockType);
	}
	
	public void load(NBTTagCompound parNBT) {
		super.load(parNBT);
		blockCountNeeded = parNBT.getInteger("blockCountNeeded");
		blockCountCurrent = parNBT.getInteger("blockCountCurrent");
		blockCoords = CoroUtilNBT.readCoords("blockCoords", parNBT);
		blockType = parNBT.getString("blockType");
	}
	
	public void save(NBTTagCompound parNBT) {
		super.save(parNBT);
		parNBT.setInteger("blockCountNeeded", blockCountNeeded);
		parNBT.setInteger("blockCountCurrent", blockCountCurrent);
		CoroUtilNBT.writeCoords("blockCoords", blockCoords, parNBT);
		parNBT.setString("blockType", blockType);
	}
}
