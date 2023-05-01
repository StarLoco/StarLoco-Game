local npc = Npc(866, 9000)

npc.colors = {16378827, 7555641, 15984024}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3704)
    end
end

RegisterNPCDef(npc)
