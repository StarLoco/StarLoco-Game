local npc = Npc(288, 1197)

npc.colors = {16777215, 0, 0}
npc.accessories = {0, 702, 952, 1711, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1115)
    end
end

RegisterNPCDef(npc)
