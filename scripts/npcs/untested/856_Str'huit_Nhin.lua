local npc = Npc(856, 30)

npc.colors = {16711680, 2949120, 1245184}
npc.accessories = {0, 701, 0, 0, 0}
npc.customArtwork = 9097

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3646, {3214, 3215, 3213})
    elseif answer == 3213 then p:ask(3647, {3216})
    elseif answer == 3216 then p:ask(3648, {3217})
    elseif answer == 3217 then p:ask(3649, {3218})
    elseif answer == 3218 then p:ask(3650, {3220, 3219})
    elseif answer == 3219 then p:ask(3651, {3221})
    elseif answer == 3221 then p:ask(3652, {3222})
    elseif answer == 3214 then p:ask(3647, {3216})
    elseif answer == 3215 then p:ask(3647, {3216})
    end
end

RegisterNPCDef(npc)
