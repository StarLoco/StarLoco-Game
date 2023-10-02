local npc = Npc(879, 41)

npc.gender = 1
npc.colors = {16767910, 16709884, 3169697}
npc.accessories = {0, 7226, 957, 0, 0}
npc.customArtwork = 9093

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3791, {3329, 3328})
    elseif answer == 3328 then p:ask(3792, {3330})
    elseif answer == 3329 then p:endDialog()
    end
end

RegisterNPCDef(npc)
