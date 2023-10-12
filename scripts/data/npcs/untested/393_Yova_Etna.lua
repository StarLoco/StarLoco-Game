local npc = Npc(393, 9009)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1640, {}, "[name]")
    end
end

RegisterNPCDef(npc)
