local npc = Npc(283, 9013)

npc.gender = 1
npc.colors = {15408938, 14435036, 10617731}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1129, {797, 798})
    elseif answer == 797 then p:ask(1131)
    elseif answer == 798 then p:ask(1132)
    end
end

RegisterNPCDef(npc)
