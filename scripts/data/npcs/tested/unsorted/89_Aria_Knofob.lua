local npc = Npc(89, 9013)

npc.gender = 1
npc.colors = {16305964, 3689830, 11171136}

local spellId = 370
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
local answers = p:spellLevel(spellId) == 0 and {1005, 1141} or {1141}
        p:ask(258, answers)
    elseif answer == 1141 then p:ask(259)
    elseif answer == 1005 then
        if p:getItem(973) then
            p:ask(260, {336})
        else
            p:ask(261)
        end
    elseif answer == 336 then
        if p:spellLevel(spellId) == 0 then
            p:setSpellLevel(spellId, 1)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
