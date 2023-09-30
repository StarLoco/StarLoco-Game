local npc = Npc(879, 41)

npc.gender = 1
npc.colors = {16767910, 16709884, 3169697}
npc.accessories = {0, 7226, 957, 0, 0}
npc.customArtwork = 9093

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3794)
    end
end

RegisterNPCDef(npc)
