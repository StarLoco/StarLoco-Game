local npc = Npc(397, 9058)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1647)
    end
end

RegisterNPCDef(npc)
