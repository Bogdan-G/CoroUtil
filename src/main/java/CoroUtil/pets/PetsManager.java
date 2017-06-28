package CoroUtil.pets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import CoroPets.ai.BehaviorModifier;
import CoroUtil.util.CoroUtilEntity;
import CoroUtil.util.CoroUtilFile;

public class PetsManager {

	private static PetsManager instance = null;
	
	public List<PetEntry> pets = new ArrayList<PetEntry>();
	public HashMap<UUID, PetEntry> lookupUUIDToPet = new HashMap<UUID, PetEntry>();
	
	public static PetsManager instance() {
		if (instance == null) {
			instance = new PetsManager();
		}
		return instance;
	}
	
	public void addPet(UUID parOwner, EntityLiving parEnt) {
		PetEntry entry = new PetEntry();
		entry.ownerUUID = parOwner;
		//entry.UUIDLeast = parEnt.getUniqueID().getLeastSignificantBits();
		//entry.UUIDMost = parEnt.getUniqueID().getMostSignificantBits();
		entry.entUUID = parEnt.getUniqueID();
		addPetEntry(entry);
	}
	
	public void addPetEntry(PetEntry parEntry) {
		pets.add(parEntry);
		lookupUUIDToPet.put(parEntry.entUUID, parEntry);
	}
	
	public void removePet(EntityLiving parEnt) {
		PetEntry entry = lookupUUIDToPet.get(parEnt.getUniqueID());
		pets.remove(entry);
		lookupUUIDToPet.remove(parEnt.getUniqueID());
	}
	
	public void hookPetInstanceReloaded(EntityCreature ent) {
		cpw.mods.fml.common.FMLLog.info("pet reloaded: " + ent);
		UUID uuid = ent.getUniqueID();
		initPetsNewInstance(ent);
	}
	
	public void hookPetInstanceUnloaded(EntityCreature ent) {
		cpw.mods.fml.common.FMLLog.info("pet unloaded: " + ent);
	}
	
	public void initPetsNewInstance(EntityCreature ent) {
		//do stuff from behavior modifiers
		PetEntry entry = lookupUUIDToPet.get(ent.getUniqueID());
		if (entry != null) {
			BehaviorModifier.tameMob(ent, entry.ownerUUID, false);
		} else {
			cpw.mods.fml.common.FMLLog.info("WARNING!!! failed to find entry for this reloaded mob that is marked tame ");
		}
	}
	
	public void nbtReadFromDisk() {
		FileInputStream fis = null;
		
    	try {
    		String URL = CoroUtilFile.getWorldSaveFolderPath() + CoroUtilFile.getWorldFolderName() + "CoroPets" + File.separator;
    		
    		File file = new File(URL + "tamedPets" + ".dat");
    		if (file.exists()) {
		    	fis = new FileInputStream(file);
		    	
		    	NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fis);
		    	
		    	nbtRead(nbttagcompound);
				
				if (fis != null) {
	    			fis.close();
	    		}
    		}
			
    	} catch (Exception ex) {
    		cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)ex, "CoroUtil stacktrace: %s", (Throwable)ex);
    	} finally {
    		
    		
    	}
	}
	
	public void nbtWriteToDisk() {
		try {
			
			NBTTagCompound nbt = nbtWrite();
			
			String URL = CoroUtilFile.getWorldSaveFolderPath() + CoroUtilFile.getWorldFolderName() + "CoroPets" + File.separator;
			
			File fl = new File(URL);
			if (!fl.exists()) fl.mkdirs();
				
			FileOutputStream fos = new FileOutputStream(URL + "tamedPets" + ".dat");
			
	    	CompressedStreamTools.writeCompressed(nbt, fos);
	    	
	    	fos.close();
			
		} catch (Exception ex) {
			cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)ex, "CoroUtil stacktrace: %s", (Throwable)ex);
		}
	}
	
	public void nbtRead(NBTTagCompound parNBT) {
		Iterator it = parNBT.func_150296_c().iterator();
        while (it.hasNext()) {
        	String tagName = (String) it.next();
        	NBTTagCompound entry = parNBT.getCompoundTag(tagName);
        	
        	PetEntry petEntry = new PetEntry();
        	petEntry.nbtRead(entry);
        	//petEntry.initLoad();
        	
        	addPetEntry(petEntry);
        }
	}
	
	public NBTTagCompound nbtWrite() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		for (int i = 0; i < pets.size(); i++) {
			NBTTagCompound nbtEntry = pets.get(i).nbtWrite();
			nbt.setTag("entry_" + i, nbtEntry);
		}
		
		return nbt;
	}
	
	public void reset() {
		pets.clear();
	}
}
