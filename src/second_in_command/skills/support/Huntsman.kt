package second_in_command.skills.support

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.skills.WolfpackTactics
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class Huntsman : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all fighters"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("+25%% damage against frigates", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun applyEffectsToFighterSpawnedByShip(fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var stats = fighter!!.mutableStats

        stats.damageToFrigates.modifyPercent(id, 25f)
    }

}