local npc = Npc(843, 30)

npc.colors = {16760576, 8205625, 16777139}
npc.accessories = {6509, 8247, 0, 0, 0}
npc.customArtwork = 9102

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3540, {3135, 3124})
    elseif answer == 3124 then p:endDialog()
    elseif answer == 3135 then p:ask(3557, {3120})
    end
end

RegisterNPCDef(npc)