local npc = Npc(843, 30)
local bBMonsterID = 972
local bNMonsterID = 973
local mPMonsterID = 974
local aIMonsterID = 982

local questID = 172

npc.colors = {16760576, 8205625, 16777139}
npc.accessories = {6509, 8247, 0, 0, 0}
npc.customArtwork = 9102

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 then 
            if p:breed() == FecaBreed then p:ask(3539, {3134, 3123})
            elseif p:breed() == OsamodasBreed then p:ask(3545, {3140, 3129})
            elseif p:breed() == EnutrofBreed then p:ask(3538, {3133, 3122})
            elseif p:breed() == SramBreed then p:ask(3547, {3142, 3131})
            elseif p:breed() == XelorBreed then p:ask(3542, {3126, 3137})
            elseif p:breed() == EcaflipBreed then p:ask(3541, {3136, 3125})
            elseif p:breed() == EniripsaBreed then p:ask(3537, {3121, 3132})
            elseif p:breed() == IopBreed then p:ask(3540, {3124, 3135})
            elseif p:breed() == CraBreed then p:ask(3536, {3118, 3119})
            elseif p:breed() == SacrierBreed then p:ask(3546, {3141, 3130})
            elseif p:breed() == PandawaBreed then p:ask(3543, {3127, 3138})
            elseif p:breed() == SadidaBreed then p:ask(3544, {3128, 3139})
            end
        elseif  answer == 3123 or answer == 3129 or answer == 3122 or answer == 3131 or answer == 3126 or
                answer == 3125 or answer == 3121 or answer == 3124 or answer == 3119 or answer == 3130 or
                answer == 3127 or answer == 3128 then p:endDialog()
        elseif  answer == 3134 or answer == 3140 or answer == 3133 or answer == 3142 or answer == 3137 or
                answer == 3136 or answer == 3132 or answer == 3135 or answer == 3118 or answer == 3141 or
                answer == 3138 or answer == 3139 then p:ask(3557,{3120})
        elseif  answer == 3120 then
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if quest:ongoingFor(p) then
        if p:breed() == FecaBreed and quest:hasCompletedObjective(p, 704) then
            p:ask(3548)
        elseif p:breed() == OsamodasBreed and quest:hasCompletedObjective(p, 710) then
            p:ask(3548)
        elseif p:breed() == EnutrofBreed and quest:hasCompletedObjective(p, 703) then
            p:ask(3548)
        elseif p:breed() == SramBreed and quest:hasCompletedObjective(p, 712) then
            p:ask(3548)
        elseif p:breed() == XelorBreed and quest:hasCompletedObjective(p, 707) then
            p:ask(3548)
        elseif p:breed() == EcaflipBreed and quest:hasCompletedObjective(p, 706) then
            p:ask(3548)
        elseif p:breed() == EniripsaBreed and quest:hasCompletedObjective(p, 702) then
            p:ask(3548)
        elseif p:breed() == IopBreed and quest:hasCompletedObjective(p, 705) then
            p:ask(3548)
        elseif p:breed() == CraBreed and quest:hasCompletedObjective(p, 701) then
            p:ask(3548)
        elseif p:breed() == SacrierBreed and quest:hasCompletedObjective(p, 711) then
            p:ask(3548)
        elseif p:breed() == PandawaBreed and quest:hasCompletedObjective(p, 709) then
            p:ask(3548)
        elseif p:breed() == SadidaBreed and quest:hasCompletedObjective(p, 708) then
            p:ask(3548)
        else
            p:ask(3634)
            return
        end
        quest:completeObjective(p, 713)
        return
    
    end

    if quest:finishedBy(p) then
        p:ask(3549)
        return
    end
    
end

RegisterNPCDef(npc)
