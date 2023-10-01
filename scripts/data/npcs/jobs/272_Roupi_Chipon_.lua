local npc = Npc(272, 9008)

npc.sales = {
    {item = 1946},
    {item = 2111}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1183, {852})
    elseif answer == 852 then p:ask(1184, {853})
    elseif answer == 853 then
        if p:tryLearnJob(FishermanJob) then
            p:ask(1185)
        else
            p:ask(1489)
        end
    end
end

RegisterNPCDef(npc)
