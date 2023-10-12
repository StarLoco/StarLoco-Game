local npc = Npc(927, 120)

npc.colors = {393216, 393216, 393216}
npc.accessories = {0, 8569, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4137, {363})
    elseif answer == 363 then p:endDialog()
    end
end

RegisterNPCDef(npc)
