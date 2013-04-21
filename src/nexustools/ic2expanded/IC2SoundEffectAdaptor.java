package nexustools.ic2expanded;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;

public class IC2SoundEffectAdaptor {

	public static boolean ic2Incompatible = false;
	public static Method ic2AudioManageCreateSourceMethod;
	public static Object ic2PositionSpecBackPackEnum;
	public static Object ic2AudioManager;
	public static Object ic2AudioManagerDefaultVolume;
	
	public int destroyTimer;
	public int allowPauseTimer = 5;
	public String soundEffectFileName;
	public boolean trySoundEffect = true;
	public int soundActionDelay = 0;
	public Object ic2AudioSourceInstance;
	public Method ic2AudioSourcePlayMethod;
	public Method ic2AudioSourcePauseMethod;
	public Method ic2AudioSourceRemoveMethod;
	public Method ic2AudioSourceUpdatePositionMethod;
	public Field ic2AudioSourceValidField;
	
	public IC2SoundEffectAdaptor(String soundFile) {
		soundEffectFileName = soundFile;
	}

	public static void init() throws Exception {
		try {
			if(ic2AudioManageCreateSourceMethod == null) {
				Class<?> ic2audioSourceManager = Class.forName("ic2.core.audio.AudioManager");
				ic2AudioManageCreateSourceMethod = ic2audioSourceManager.getDeclaredMethod("createSource", Object.class, Class.forName("ic2.core.audio.PositionSpec"), String.class, boolean.class, boolean.class, float.class);
				Class<?> ic2PositionSpec = Class.forName("ic2.core.audio.PositionSpec");
				ic2PositionSpecBackPackEnum = null;
				for(Object constant : ic2PositionSpec.getEnumConstants())
					if(constant.toString().equals("Backpack")) {
						ic2PositionSpecBackPackEnum = constant;
						break;
					}
	
				if(ic2PositionSpecBackPackEnum == null)
					throw new Exception("ic2PositionSpecBackPackEnum not found.");
	
				Class<?> ic2MainClass = Class.forName("ic2.core.IC2");
				Field ic2AudioManagerField = ic2MainClass.getDeclaredField("audioManager");
				ic2AudioManager = ic2AudioManagerField.get(null);
	
				Field ic2AudoManagerDefaultVolumeField = ic2audioSourceManager.getDeclaredField("defaultVolume");
				ic2AudioManagerDefaultVolume = ic2AudoManagerDefaultVolumeField.get(ic2AudioManager);
			}
		}catch(Throwable t){
			ic2Incompatible = true;
			throw new Exception("IC2 Core Incompatible", t);
		}
			
	}
	
	public void tick(boolean playing, EntityPlayer p) {
		if(ic2Incompatible || !trySoundEffect)
			return;
		
		try {
			init();
			if(allowPauseTimer > 0) {
				allowPauseTimer--;
				playing = true;
			}
			
			if(playing) {
				if(ic2AudioSourceInstance == null || !ic2AudioSourceValidField.getBoolean(ic2AudioSourceInstance)) {
					ic2AudioSourceInstance = ic2AudioManageCreateSourceMethod.invoke(ic2AudioManager, p, ic2PositionSpecBackPackEnum, soundEffectFileName, true, false, ic2AudioManagerDefaultVolume);
					
					if(ic2AudioSourceInstance != null) {
						if(ic2AudioSourceUpdatePositionMethod == null) {
							Class<?> ic2AudioSource = ic2AudioSourceInstance.getClass();

							ic2AudioSourcePlayMethod = ic2AudioSource.getDeclaredMethod("play");
							ic2AudioSourcePauseMethod = ic2AudioSource.getDeclaredMethod("pause");
							ic2AudioSourceRemoveMethod = ic2AudioSource.getDeclaredMethod("remove");
							ic2AudioSourceUpdatePositionMethod = ic2AudioSource.getDeclaredMethod("updatePosition");
							ic2AudioSourceValidField = ic2AudioSource.getDeclaredField("valid");
							ic2AudioSourceValidField.setAccessible(true); // ignore private
						}
					}
				}

				destroyTimer = 50;
				ic2AudioSourcePlayMethod.invoke(ic2AudioSourceInstance);
			} else if(ic2AudioSourceInstance != null) {
				if(destroyTimer > 0) {
					ic2AudioSourcePauseMethod.invoke(ic2AudioSourceInstance);
					destroyTimer --;
				} else {
					ic2AudioSourceRemoveMethod.invoke(ic2AudioSourceInstance);
					ic2AudioSourceInstance = null;
				}
			}
		
			if(playing && ic2AudioSourceInstance != null)
				ic2AudioSourceUpdatePositionMethod.invoke(ic2AudioSourceInstance);
		}catch(Throwable t) {
			t.printStackTrace();
			trySoundEffect = false;
		}
	}

}
