package CoroUtil.bt.actions;

import java.util.Random;

import CoroUtil.bt.Behavior;
import CoroUtil.bt.EnumBehaviorState;
import CoroUtil.bt.leaf.LeafAction;

import org.bogdang.modifications.random.XSTR;

public class RandomChance extends LeafAction {

	public RandomChance(Behavior parParent) {
		super(parParent);
	}
	
	@Override
	public EnumBehaviorState tick() {
		Random rand = new XSTR();
		Boolean bool = rand.nextBoolean();
		//bool = false;
		dbg("Leaf Rand Tick - " + bool);
		return bool ? EnumBehaviorState.SUCCESS : EnumBehaviorState.FAILURE;
	}
	
}
