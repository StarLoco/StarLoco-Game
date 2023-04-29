local npc = Npc(63, 9037)

npc.sales = {
    {item=498},
    {item=499},
    {item=500}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(37, {275})
    elseif answer == 275 then p:ask(341, {350, 351, 352})
    elseif answer == 350 then p:ask(426, {353})
    elseif answer == 353 then
        if p:tryLearnJob(WandCarver) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 351 then p:ask(427, {354})
    elseif answer == 354 then
        if p:tryLearnJob(BowCarver) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 352 then p:ask(428, {355})
    elseif answer == 355 then
        if p:tryLearnJob(StaffCarver) then
            p:ask(335)
        else
            p:ask(336)
        end
    end
end

RegisterNPCDef(npc)
