local npc = Npc(880, 20)

npc.colors = {5855514, 16777215, 9847110}
npc.accessories = {0, 0, 8534, 0, 0}
npc.customArtwork = 9103

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3798, {3331, 333})
    elseif answer == 3331 then p:endDialog()
    elseif answer == 333 then p:ask(414)
    end
end

RegisterNPCDef(npc)