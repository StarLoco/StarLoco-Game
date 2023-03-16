local npc = Npc(857, 30)

npc.colors = {6513587, 16771366, 13749677}
npc.accessories = {0, 6988, 0, 0, 0}
npc.customArtwork = 9089

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3655, {3223})
    elseif answer == 3223 then p:ask(3656, {3224, 3225})
    end
end

RegisterNPCDef(npc)