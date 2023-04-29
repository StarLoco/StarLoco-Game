local npc = Npc(62, 9015)

npc.sales = {
    {item=493},
    {item=494},
    {item=495},
    {item=496}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(38, {508, 341})
    elseif answer == 508 then p:ask(604)
    elseif answer == 341 then p:ask(421, {384, 345, 343, 344, 342})
    elseif answer == 384 then p:ask(456, {385})
    elseif answer == 385 then
        if p:tryLearnJob(AxeSmith) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 345 then p:ask(425, {349})
    elseif answer == 349 then
        if p:tryLearnJob(HammerSmith) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 343 then p:ask(423, {346})
    elseif answer == 346 then
        if p:tryLearnJob(DaggerSmith) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 344 then p:ask(424, {348})
    elseif answer == 348 then
        if p:tryLearnJob(ShovelSmith) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 342 then p:ask(422, {347})
    elseif answer == 347 then
        if p:tryLearnJob(SwordSmith) then
            p:ask(335)
        else
            p:ask(336)
        end
    end
end

RegisterNPCDef(npc)
