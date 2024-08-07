package second_in_command.specs

import com.fs.starfarer.api.Global
import org.apache.log4j.Level
import org.lazywizard.lazylib.ext.json.getFloat
import second_in_command.SCUtils
import second_in_command.misc.loadTextureCached
import java.awt.Color

object SCSpecStore {

    var logger = Global.getLogger(this::class.java).apply { level = Level.ALL }

    private var aptitudeSpecs = ArrayList<SCAptitudeSpec>()
    fun getAptitudeSpecs() = aptitudeSpecs
    fun getAptitudeSpec(specId: String) = aptitudeSpecs.find { it.id == specId }

    private var skillSpecs = ArrayList<SCSkillSpec>()
    fun getSkillSpecs() = skillSpecs
    fun getSkillSpec(specId: String) = skillSpecs.find { it.id == specId }

    fun loadAptitudeSpecsFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/secondInCommand/SCAptitudes.csv", SCUtils.MOD_ID)

        for (index in 0 until  CSV.length())
        {
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            val name = row.getString("name")
            val category = row.getString("category")


            val spawnWeight = row.getFloat("spawnWeight")



            val colorString = row.getString("color")
            val cs = colorString.split(",").map { it.trim().toInt() }
            val color = Color(cs[0], cs[1], cs[2], cs[3])

            var tags = row.getString("tags").split(",").map { it.trim() }

            val pluginPath = row.getString("plugin")

            var spec = SCAptitudeSpec(id, name, category, spawnWeight, color, tags, pluginPath)
            aptitudeSpecs.add(spec)
        }

        logger.debug("Second in Command: Loaded ${aptitudeSpecs.count()} Aptitude Specs.")
    }

    fun loadSkillSpecsFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/secondInCommand/SCSkills.csv", SCUtils.MOD_ID)

        var order = 0
        for (index in 0 until  CSV.length())
        {
            order++
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            val name = row.getString("name")
            val iconPath = row.getString("iconPath")
            Global.getSettings().loadTextureCached(iconPath)

            val npcSpawnWeightString = row.getString("npcSpawnWeight")
            var npcSpawnWeight = 0f
            if (npcSpawnWeightString != "") npcSpawnWeight = npcSpawnWeightString.toFloat()



            val pluginPath = row.getString("plugin")

            var spec = SCSkillSpec(id, name, iconPath, npcSpawnWeight, order, pluginPath)
            skillSpecs.add(spec)
        }

        logger.debug("Second in Command: Loaded ${skillSpecs.count()} Skill Specs.")
    }






}