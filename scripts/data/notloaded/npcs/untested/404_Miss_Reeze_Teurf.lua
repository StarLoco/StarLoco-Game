local npc = Npc(404, 9069)

npc.gender = 1
npc.colors = {14090508, 16777215, 15575152}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1649)
    end
end

RegisterNPCDef(npc)
