local npc = Npc(1104, 40)

npc.colors = {5281247, 6716152, 8631002}
npc.accessories = {0, 6481, 2547, 2077, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6404, {5677, 5919, 567})
    elseif answer == 5677 then p:ask(6405)
    elseif answer == 5919 then p:ask(4553)
    end
end

RegisterNPCDef(npc)
