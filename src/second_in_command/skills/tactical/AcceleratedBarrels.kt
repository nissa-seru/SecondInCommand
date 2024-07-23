package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class AcceleratedBarrels : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("+10%% increased non-missile weapon rate of fire", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-10%% increased non-missile weapon flux usage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.ballisticRoFMult.modifyMult(id, 1.1f)
        stats.energyRoFMult.modifyMult(id, 1.1f)

        stats!!.ballisticWeaponFluxCostMod.modifyMult(id, 0.9f)
        stats!!.energyWeaponFluxCostMod.modifyMult(id, 0.9f)
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}