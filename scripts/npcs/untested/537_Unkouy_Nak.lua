local npc = Npc(537, 80)

npc.colors = {16777215, 0, 0}
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2284, {191})
    elseif answer == 191 then p:endDialog()
    end
end

RegisterNPCDef(npc)
