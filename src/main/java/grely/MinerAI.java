package main.java.grely;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.units.AIController;
import mindustry.gen.Call;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class MinerAI extends mindustry.ai.types.MinerAI {
    @Override
    public void updateMovement() {
        CoreBlock.CoreBuild core = this.unit.closestCore();
        if (!this.unit.canMine() || core == null) {
            return;
        }
        if (!this.unit.validMine(this.unit.mineTile)) {
            this.unit.mineTile(null);
        }
        if (this.mining) {
            if (this.timer.get(1, 24.0f) || this.targetItem == null) {
                this.targetItem = this.unit.type.mineItems.min(i -> Vars.indexer.hasOre((Item)i) && this.unit.canMine((Item)i), i -> core.items.get((Item)i));
            }
            if (this.targetItem != null && core.acceptStack(this.targetItem, 1, this.unit) == 0) {
                this.unit.clearItem();
                this.unit.mineTile = null;
                return;
            }
            if (this.unit.stack.amount >= this.unit.type.itemCapacity || this.targetItem != null && !this.unit.acceptsItem(this.targetItem)) {
                this.mining = false;
            } else {
                if (this.timer.get(2, 60.0f) && this.targetItem != null) {
                    this.ore = Vars.indexer.findClosestOre(this.unit, this.targetItem);
                }
                if (this.ore != null) {
                    this.moveTo(this.ore, this.unit.type.mineRange / 2.0f, 20.0f);
                    if (this.ore.block() == Blocks.air && this.unit.within(this.ore, this.unit.type.mineRange)) {
                        this.unit.mineTile = this.ore;
                    }
                    if (this.ore.block() != Blocks.air) {
                        this.mining = false;
                    }
                }
            }
        } else {
            this.unit.mineTile = null;
            if (this.unit.stack.amount == 0) {
                this.mining = true;
                return;
            }
            if (this.unit.within(core, this.unit.type.range)) {
                if (core.acceptStack(this.unit.stack.item, this.unit.stack.amount, this.unit) > 0) {
                    Call.transferItemTo(this.unit, this.unit.stack.item, this.unit.stack.amount, this.unit.x, this.unit.y, core);
                }
                this.unit.clearItem();
                this.mining = true;
            }
            this.circle(core, this.unit.type.range / 1.8f);
        }
    }
}
