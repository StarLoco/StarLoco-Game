local npc = Npc(394, 9070)

npc.gender = 1
npc.colors = {3187242, 12232978, 11610899}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1644)
    end
end

RegisterNPCDef(npc)
