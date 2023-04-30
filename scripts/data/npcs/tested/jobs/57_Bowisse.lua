local npc = Npc(57, 9034)

npc.sales = {
    {item=579}
}

npc.barters = {
    {to={itemID=579, quantity= 1}, from= {
        {itemID=367, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(29, {81, 377, 326, 82})
    elseif answer == 81 then p:ask(85, {630})
    elseif answer == 630 then p:ask(690, {636})
    elseif answer == 636 then p:ask(693)
    elseif answer == 82 then p:ask(86)
    elseif answer == 326 then p:ask(408, {327, 328})
    elseif answer == 327 then
        if p:tryLearnJob(Shoemaker) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 328 then p:ask(409)
    elseif answer == 377 then p:ask(450, {378})
    elseif answer == 378 then p:ask(451)
    end
end

RegisterNPCDef(npc)
