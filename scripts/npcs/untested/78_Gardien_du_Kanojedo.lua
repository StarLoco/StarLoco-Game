local npc = Npc(78, 1010)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(411, {334, 1269, 333})
    elseif answer == 1269 then p:ask(108)
    elseif answer == 333 then p:ask(414)
    elseif answer == 334 then p:ask(415, {337, 338, 340})
    elseif answer == 337 then p:ask(417)
    elseif answer == 338 then p:ask(418)
    elseif answer == 340 then p:ask(420)
    end
end

RegisterNPCDef(npc)
