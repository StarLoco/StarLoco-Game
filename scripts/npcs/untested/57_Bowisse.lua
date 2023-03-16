local npc = Npc(57, 9034)

npc.sales = {
    {item=579}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(29, {81, 377, 326, 82})
    elseif answer == 81 then p:ask(85, {630})
    elseif answer == 82 then p:ask(86)
    elseif answer == 326 then p:ask(408, {327, 328})
    elseif answer == 377 then p:ask(450, {378})
    end
end

RegisterNPCDef(npc)
