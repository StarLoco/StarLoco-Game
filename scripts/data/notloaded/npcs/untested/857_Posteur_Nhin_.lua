local npc = Npc(856, 30)

npc.colors = {16711680, 2949120, 1245184}
npc.accessories = {0, 0xbd, 0, 0, 0}
npc.customArtwork = 9097

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3646, {3223})
    elseif answer == 3223 then p:ask(3656, {3224, 3225})
    elseif answer == 3224 then p:ask(3657, {3226})
    end
end

RegisterNPCDef(npc)
