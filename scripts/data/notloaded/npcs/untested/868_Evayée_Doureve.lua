local npc = Npc(868, 101)

npc.gender = 1
npc.colors = {16310700, 15239964, 11627841}
npc.accessories = {0, 703, 0, 0, 0}
npc.customArtwork = 9099

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3706, {3248})
    elseif answer == 3248 then p:ask(3707, {3250, 3249})
    elseif answer == 3249 then p:ask(3708, {3252, 3251})
    elseif answer == 3251 then p:ask(3709, {3253})
    elseif answer == 3253 then p:endDialog()
    end
end

RegisterNPCDef(npc)
