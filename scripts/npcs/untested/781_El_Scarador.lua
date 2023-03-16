local npc = Npc(781, 91)

npc.gender = 1
npc.accessories = {0, 8116, 8117, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3179, {2947})
    end
end

RegisterNPCDef(npc)
