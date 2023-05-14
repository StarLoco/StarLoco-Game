local npc = Npc(431, 21)

npc.gender = 1
npc.colors = {16488451, 16777215, -1}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        p:ask(1736, {1370, 1381})
    elseif answer == 1370 then
        p:ask(1723)
    elseif answer == 1381 then
        p:ask(1747)
    end
end

RegisterNPCDef(npc)
