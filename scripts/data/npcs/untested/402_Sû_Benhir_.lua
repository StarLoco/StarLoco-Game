local npc = Npc(402, 9010)

npc.colors = {13482609, 8158332, 13619151}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1662)
    end
end

RegisterNPCDef(npc)
