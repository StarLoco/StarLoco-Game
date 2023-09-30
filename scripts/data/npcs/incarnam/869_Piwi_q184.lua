local npc = Npc(869, 1212)
--TODO: Lié à la quête 184
npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3710, {3262, 3260})
    elseif answer == 3260 then p:ask(3723, {3264, 326})
    elseif answer == 3264 then p:endDialog()
    elseif answer == 326 then p:ask(408, {327, 328})
    elseif answer == 328 then p:ask(409)
    elseif answer == 3262 then p:ask(3724, {3265, 326})
    elseif answer == 3265 then p:ask(3710, {3262, 3260})
    elseif answer == 326 then p:ask(408, {327, 328})
    end
end

RegisterNPCDef(npc)
