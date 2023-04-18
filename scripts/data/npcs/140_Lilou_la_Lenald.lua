local npc = Npc(140, 9052)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(465)
    end
end

RegisterNPCDef(npc)
