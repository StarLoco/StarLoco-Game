local npc = Npc(1088, 9097)

npc.customArtwork = 9019

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6169, {6023, 23016})
    elseif answer == 6023 then p:endDialog()
    end
end

RegisterNPCDef(npc)
