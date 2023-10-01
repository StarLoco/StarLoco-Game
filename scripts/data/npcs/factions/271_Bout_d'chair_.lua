local npc = Npc(271, 1207)
--TODO: Lié à la quête Alignement Brâkmar 4
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1105, {848})
    elseif answer == 848 then
        p:ask(1178, {849})
    elseif answer == 849 then
        if p:tryLearnJob(ButcherJob) then
            p:ask(1179)
        else
            p:ask(1481)
        end
    end
end

RegisterNPCDef(npc)
