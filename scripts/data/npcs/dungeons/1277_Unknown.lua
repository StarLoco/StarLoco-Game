local npc = Npc(1277, 9217)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        p:ask(7673, {7694})
    elseif answer == 7694 then
        p:endDialog()
    end
end

RegisterNPCDef(npc)
