local npc = Npc(136, 9053)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(462, {388})
    elseif answer == 388 then
        p:teleport(167, 273)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
