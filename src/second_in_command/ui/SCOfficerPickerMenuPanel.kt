package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import second_in_command.SCData
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*
import second_in_command.ui.panels.PickerBackgroundPanelPlugin
import second_in_command.ui.tooltips.OfficerTooltipCreator
import second_in_command.ui.tooltips.SCSkillTooltipCreator

class SCOfficerPickerMenuPanel(var menu: SCSkillMenuPanel, var originalPickerElement: SCOfficerPickerElement, var subpanelParent: CustomPanelAPI, var slotId: Int, var data: SCData, var isAtColony: Boolean) {



    var selectedOfficer: SCOfficer? = null

    fun init() {
        var plugin = PickerBackgroundPanelPlugin(menu.panel)

        var width = menu.width - 25
        var height = menu.height - 25

        var heightCap = 70f

        var popupPanel = menu.panel.createCustomPanel(width, height, plugin)
        plugin.panel = popupPanel
        menu.panel.addComponent(popupPanel)
        popupPanel.position.inMid()

        var scrollerPanel = popupPanel.createCustomPanel(width, height - heightCap, null)
        popupPanel.addComponent(scrollerPanel)
        scrollerPanel.position.inTL(0f, 0f)
        var scrollerElement = scrollerPanel.createUIElement(width, height - heightCap, true)

        var officers = data.getOfficersInFleet()
        var activeOfficers = data.getAssignedOfficers()

        for (officer in officers) {

            var aptitudeSpec = SCSpecStore.getAptitudeSpec(officer.aptitudeId)
            var aptitudePlugin = aptitudeSpec!!.getPlugin()

            scrollerElement.addSpacer(10f)
            var officerElement = scrollerElement.addLunaElement(width - 10, 96f + 36).apply {
                enableTransparency = true
                backgroundAlpha = 0.025f
                borderAlpha = 0.1f
                backgroundColor = aptitudePlugin.getColor()
                borderColor = aptitudePlugin.getColor()
            }


            officerElement.advance {
                if (officer == selectedOfficer) {
                    officerElement.backgroundAlpha = 0.15f
                    officerElement.borderAlpha = 0.35f
                }
                else if (officerElement.isHovering) {
                    officerElement.backgroundAlpha = 0.1f
                    officerElement.borderAlpha = 0.25f
                }
                else {
                    officerElement.backgroundAlpha = 0.025f
                    officerElement.borderAlpha = 0.1f
                }
            }



            officerElement.onClick { selectOfficer(officer) }


            var inner = officerElement.innerElement
            inner.addSpacer(24f)

            var officerPickerElement = SCOfficerPickerElement(officer.person, aptitudePlugin.getColor(), inner, 96f, 96f)
            officerPickerElement.onClick { selectOfficer(officer) }

            var paraElement = inner.addLunaElement(100f, 20f).apply {
                renderBorder = false
                renderBackground = false
            }
            paraElement.elementPanel.position.aboveMid(officerPickerElement.elementPanel, 0f)

            paraElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
            var aptitudePara = paraElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
            aptitudePara.position.inTL(paraElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 - 1, paraElement.height  -aptitudePara.computeTextHeight(aptitudePara.text)-5)


           /* officerPickerElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
            var aptitudePara = officerPickerElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
            aptitudePara.position.inTL(officerPickerElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 - 1, -aptitudePara.computeTextHeight(aptitudePara.text)-5)

            */

            scrollerElement.addTooltipTo(OfficerTooltipCreator(officer, isAtColony), officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)

            var offset = 10f
            var offsetElement = inner.addLunaElement(0f, 0f)
            offsetElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)

            var background = AptitudeBackgroundElement(aptitudePlugin.getColor(), inner, true)
            //background.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)
            background.elementPanel.position.belowLeft(offsetElement.elementPanel, offset)

            var officerUnderline = SkillUnderlineElement(aptitudePlugin.getColor(), 2f, inner, 96f)
            officerUnderline.position.belowLeft(officerPickerElement.elementPanel, 2f)

            aptitudePlugin.clearSections()
            aptitudePlugin.createSections()
            var sections = aptitudePlugin.getSections()

            var originSkill = SCSpecStore.getSkillSpec(aptitudePlugin.getOriginSkillId())
            var originSkillElement = SkillWidgetElement(originSkill!!.id, true, false, true, originSkill!!.iconPath, "leadership1", aptitudePlugin.getColor(), inner, 72f, 72f)
            inner.addTooltipTo(SCSkillTooltipCreator(data, originSkill.getPlugin(), aptitudePlugin, 0, false), originSkillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)
            //originSkillElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)
            originSkillElement.elementPanel.position.rightOfMid(background.elementPanel, 20f)

            originSkillElement.onClick { selectOfficer(officer) }


            var originGap = SkillGapElement(aptitudePlugin.getColor(), inner)
            originGap.elementPanel.position.rightOfTop(originSkillElement.elementPanel, 0f)
            originGap.renderArrow = true

            var previousSections = ArrayList<SCAptitudeSection>()
            var skillElements = ArrayList<SkillWidgetElement>()
            var previous: CustomPanelAPI = originGap.elementPanel
            for (section in sections) {

                var isLastSection = sections.last() == section
                var canOnlyChooseOne = !section.canChooseMultiple

                var firstSkillThisSection: SkillWidgetElement? = null
                var usedWidth = 0f

                section.previousUISections.addAll(previousSections)
                previousSections.add(section)

                var skills = section.getSkills()
                for (skill in skills) {
                    var skillSpec = SCSpecStore.getSkillSpec(skill)
                    var skillPlugin = skillSpec!!.getPlugin()

                    var isFirst = skills.first() == skill
                    var isLast = skills.last() == skill

                    var preacquired = false
                    var activated = false
                    if (officer.activeSkillIDs.contains(skill)) {
                        preacquired = true
                        activated = true
                    }

                    var skillElement = SkillWidgetElement(skill, activated, false, preacquired, skillPlugin!!.getIconPath(), section.soundId, aptitudePlugin.getColor(), inner, 72f, 72f)
                    skillElement.onClick { selectOfficer(officer) }
                    skillElements.add(skillElement)
                    section.activeSkillsInUI.add(skillElement)
                    usedWidth += 72f

                    var tooltip = SCSkillTooltipCreator(data, skillPlugin, aptitudePlugin, section.requiredPreviousSkills, !section.canChooseMultiple)
                    inner.addTooltipTo(tooltip, skillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)
                    section.tooltips.add(tooltip)

                    if (firstSkillThisSection == null) {
                        firstSkillThisSection = skillElement
                    }

                    if (isFirst) {
                        skillElement.elementPanel.position.rightOfTop(previous, 0f)
                    } else {
                        skillElement.elementPanel.position.rightOfTop(previous, 3f)
                        usedWidth += 3f
                    }



                    if (!isLast) {
                        var seperator = SkillSeperatorElement(aptitudePlugin.getColor(), inner)
                        seperator.elementPanel.position.rightOfTop(skillElement.elementPanel, 3f)
                        previous = seperator.elementPanel
                        usedWidth += 3f
                    }
                    else if (!isLastSection) {
                        var gap = SkillGapElement(aptitudePlugin.getColor(), inner)
                        gap.elementPanel.position.rightOfTop(skillElement.elementPanel, 0f)
                        previous = gap.elementPanel

                        var nextIndex = sections.indexOf(section) + 1
                        var nextSection = sections.getOrNull(nextIndex)
                        if (nextSection != null) {
                            nextSection.uiGap = gap
                        }

                    }

                    if (canOnlyChooseOne) {
                        var underline = SkillUnderlineElement(aptitudePlugin.getColor(), 2f, inner, usedWidth)
                        underline.position.belowLeft(firstSkillThisSection.elementPanel, 2f)
                    }


                }
            }

            var paraAnchorElement = inner.addLunaElement(0f, 0f)
            paraAnchorElement.position.aboveLeft(originSkillElement.elementPanel, 6f)

            var spRemaining = menu.calculateRemainingSP(officer, skillElements)
            var spHighlight = Misc.getHighlightColor()
            if (spRemaining <= 0) spHighlight = Misc.getGrayColor()

            var officerParaTextExtra = ""
            var minusText = ""

            if (officerAlreadySlotted(officer)) officerParaTextExtra = "This officer is already assigned."
            else if (doesOffficerMatchExistingAptitude(officer)) officerParaTextExtra = "Can't assign two officers of the same aptitude."
            else if (doesOffficerMatchCategory(officer)) officerParaTextExtra = "Can't assign two aptitudes of the same category."

            if (officerParaTextExtra != "") minusText = "-"

            var officerPara = inner.addPara("${officer.person.nameString} - $spRemaining SP $minusText $officerParaTextExtra", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$spRemaining")
            officerPara.position.rightOfBottom(paraAnchorElement.elementPanel, 0f)

            officerPara.setHighlight("$spRemaining", officerParaTextExtra)
            officerPara.setHighlightColors(spHighlight, Misc.getNegativeHighlightColor())

            calculateSectionRequirements(officer, sections, skillElements)


        }

        scrollerElement.addSpacer(10f)

        scrollerPanel.addUIElement(scrollerElement)

        var buttonPanel = popupPanel.createCustomPanel(width, heightCap, null)
        popupPanel.addComponent(buttonPanel)
        buttonPanel.position.belowLeft(scrollerPanel, 0f)

        var buttonElement = buttonPanel.createUIElement(width, height, false)
        buttonElement.position.inTL(0f, 0f)
        buttonPanel.addUIElement(buttonElement)
        buttonElement.addPara("", 0f)

        var confirmButton = ConfirmCancelButton(Misc.getGrayColor(), buttonElement, 120f, 35f).apply {
            addText("Confirm")
            centerText()
            blink = false
            position.inTR(150f, 14f)
        }

        confirmButton.advance {
            if (selectedOfficer != null) {
                var plugin = SCSpecStore.getAptitudeSpec(selectedOfficer!!.aptitudeId)!!.getPlugin()
                confirmButton.color = plugin.getColor()
                confirmButton.blink = true
            }
        }

        confirmButton.onClick {

            if (selectedOfficer == null) {
                Global.getSoundPlayer().playUISound("ui_button_disabled_pressed", 1f, 1f)
                return@onClick
            }

            confirmButton.playClickSound()
            menu.panel.removeComponent(popupPanel)

            menu.checkToApplyCRPenalty()

            var previousOfficerInSlot = data.getOfficerInSlot(slotId)
            data.setOfficerInSlot(slotId, selectedOfficer!!)
          /*  if (previousOfficerInSlot != null) {
                var skills = previousOfficerInSlot.getActiveSkillPlugins()

                if (Global.getSector().playerFleet?.fleetData != null) {
                    for (skill in skills) {
                        skill.onDeactivation(data)
                    }
                    Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
                }
            }*/


            //data.setOfficerInSlot(slotId, selectedOfficer!!)



          /*  if (Global.getSector().playerFleet?.fleetData != null) {
                var skills = selectedOfficer!!.getActiveSkillPlugins()
                for (skill in skills) {
                    skill.onActivation(data)
                }
                Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
            }*/


            menu.recreateAptitudeRow(subpanelParent, data.getOfficerInSlot(slotId), slotId)
        }

        var cancelButton = ConfirmCancelButton(Misc.getBasePlayerColor(), buttonElement, 120f, 35f).apply {
            addText("Cancel")
            centerText()
            blink = false
            position.rightOfTop(confirmButton.elementPanel, 10f)
        }

        cancelButton.onClick {
            cancelButton.playClickSound()
            menu.panel.removeComponent(popupPanel)
        }


    }

    fun selectOfficer(officer: SCOfficer) {

        if (officerAlreadySlotted(officer) || doesOffficerMatchExistingAptitude(officer) || officerAlreadySlotted(officer)) {
            Global.getSoundPlayer().playUISound("ui_button_disabled_pressed", 1f, 1f)
            return
        }

        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
        selectedOfficer = officer
    }


    fun doesOffficerMatchExistingAptitude(officer: SCOfficer) : Boolean {

        for (active in data.getAssignedOfficers()) {
            if (active == null) continue
            if (active.person == originalPickerElement.officer) continue
            if (active.aptitudeId == officer.aptitudeId) return true
        }

        return false
    }

    fun doesOffficerMatchCategory(officer: SCOfficer) : Boolean {
        for (active in data.getAssignedOfficers()) {
            if (active == null) continue
            if (active.person == originalPickerElement.officer) continue
            var spec = SCSpecStore.getAptitudeSpec(officer.aptitudeId)
            var activeSpec = SCSpecStore.getAptitudeSpec(active.aptitudeId)
            if (spec!!.category == "") continue
            if (activeSpec!!.category == spec.category) return true
        }

        return false
    }

    fun officerAlreadySlotted(officer: SCOfficer) : Boolean {
        return data.getAssignedOfficers().contains(officer)
    }

    fun getActiveSkillCount(sections: ArrayList<SCAptitudeSection>) : Int {
        return sections.sumOf { it.activeSkillsInUI.count { it.activated } }
    }

    fun calculateSectionRequirements(officer: SCOfficer, sections: ArrayList<SCAptitudeSection>, skillElements: ArrayList<SkillWidgetElement>) {
        for (section in sections) {

            var count = getActiveSkillCount(section.previousUISections)

            section.uiGap?.renderArrow = section.requiredPreviousSkills <= count
            section.tooltips.forEach { it.sectionMeetsRequirements = section.requiredPreviousSkills <= count }

        }
    }

}