local npc = Npc(805, 1535)

npc.colors = {16776986, 3033, 15862796}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3320)
    end
end

RegisterNPCDef(npc)
