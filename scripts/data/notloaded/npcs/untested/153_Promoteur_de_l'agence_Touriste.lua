local npc = Npc(153, 9053)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(537, {563, 504, 473})
    elseif answer == 563 then p:ask(4331, {3807})
    elseif answer == 473 then p:ask(560, {474})
    elseif answer == 474 then p:ask(561)
    end
end

RegisterNPCDef(npc)
