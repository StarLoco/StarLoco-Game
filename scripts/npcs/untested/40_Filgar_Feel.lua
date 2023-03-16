local npc = Npc(40, 9025)

npc.sales = {
    {item=1324},
    {item=494}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(93, {164})
    elseif answer == 164 then p:ask(199, {165})
    end
end

RegisterNPCDef(npc)
