local npc = Npc(824, 9041)

npc.colors = {10365907, 9273566, 714715}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3439)
    end
end

RegisterNPCDef(npc)
