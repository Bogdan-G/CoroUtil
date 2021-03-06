package CoroUtil.quest.quests;

import cpw.mods.fml.common.eventhandler.Event;
import CoroUtil.quest.EnumQuestState;
import CoroUtil.quest.PlayerQuests;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class KillEntityQuest extends ActiveQuest {
	
	public Class neededMob;
	public int neededKillCount;
	public boolean returnToQuestGiver;
	
	public int curKillCount;

	public KillEntityQuest() {
		questType = "killEntity";
	}
	
	public void initCustomData(Class mob, int count, boolean parReturnToQuestGiver) {
		super.initCustomData();

		neededMob = mob;
		neededKillCount = count;
		returnToQuestGiver = parReturnToQuestGiver;
	}

	@Override
	public void tick() {
		super.tick();
		
		if (curState == EnumQuestState.ASSIGNED) {
			
			if (curKillCount >= neededKillCount) {
				if (returnToQuestGiver) {
					setState(EnumQuestState.CONCLUDING);
				} else {
					setState(EnumQuestState.COMPLETE);
					cpw.mods.fml.common.FMLLog.info("quest kill complete");
				}
			}
		} else if (curState == EnumQuestState.CONCLUDING) {
			//logic that determines they have talked to the quest giver to complete the quest, should this be here or in the koa?
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if (event instanceof LivingDeathEvent) {
			deathEvent((LivingDeathEvent)event);
		}
	}
	
	public void deathEvent(LivingDeathEvent event) {
		Entity source = event.source.getEntity();
		if (source != null && source.equals(playerQuests.getPlayer()) && event.entityLiving.getClass().equals(neededMob)) {
			curKillCount++;
			saveAndSync();
			cpw.mods.fml.common.FMLLog.info("quest kill inc");
		}
	}
	
	public void load(NBTTagCompound parNBT) {
		super.load(parNBT);
		curKillCount = parNBT.getInteger("curKillCount");
		neededKillCount = parNBT.getInteger("neededKillCount");
	}
	
	public void save(NBTTagCompound parNBT) {
		super.save(parNBT);
		parNBT.setInteger("curKillCount", curKillCount);
		parNBT.setInteger("neededKillCount", neededKillCount);
	}
}
