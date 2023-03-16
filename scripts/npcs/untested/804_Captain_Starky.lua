local npc = Npc(804, 9004)

npc.colors = {7104256, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3314)
    end
end

RegisterNPCDef(npc)
