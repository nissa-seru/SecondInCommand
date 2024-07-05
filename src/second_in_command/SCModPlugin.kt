package second_in_command

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import second_in_command.misc.VanillaSkillsUtil
import second_in_command.scripts.SkillPanelReplacerScript
import second_in_command.specs.SCSpecStore

class SCModPlugin : BaseModPlugin() {

    override fun onApplicationLoad() {
        SCSpecStore.loadAptitudeSpecsFromCSV()
        SCSpecStore.loadSkillSpecsFromCSV()
    }

    override fun onGameLoad(newGame: Boolean) {
        Global.getSector().addTransientScript(SkillPanelReplacerScript())

        Global.getSector().addTransientListener(SCCampaignEventListener())
    }
}