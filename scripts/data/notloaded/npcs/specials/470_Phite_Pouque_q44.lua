local npc = Npc(470, 101)
--TODO: Lié à la quête 44
npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2265, {1898})
    elseif answer == 1898 then p:endDialog()
    end
end

RegisterNPCDef(npc)
