local npc = Npc(913, 9097)

npc.customArtwork = 9019

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4055, {3531})
    elseif answer == 3531 then p:ask(4056)
    end
end

RegisterNPCDef(npc)
