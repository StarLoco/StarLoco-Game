local npc = Npc(1045, 70)

npc.gender = 0
npc.colors = {3131087, 11897417, -1}
npc.accessories = {0, 2155, 2156, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5549, {4739, 4740})
    elseif answer == 4751 then p:ask(5634, {4759, 4760})
    elseif answer == 4762 then p:ask(5635, {4773, 4767})
    elseif answer == 4766 then p:ask(5632, {4766, 4765})
    elseif answer == 4765 then p:ask(5639, {4772, 4771})
    elseif answer == 4772 then p:ask(5643, {4783, 4784})
    elseif answer == 4763 then p:ask(5638, {4773, 4767})
    elseif answer == 4739 or answer == 4758 then p:ask(5625, {4742, 4741})
    elseif answer == 4742 or answer == 4780 then p:ask(5628, {4748, 4747})
    elseif answer == 4752 or answer == 4753 then p:ask(5635, {4762, 4761})
    elseif answer == 4741 or answer == 4784 then p:ask(5627, {4745, 4746})
    elseif answer == 4750 or answer == 4764 then p:ask(5633, {4758, 4757})
    elseif answer == 4740 or answer == 4783 then p:ask(5626, {4744, 4743})
    elseif answer == 4760 or answer == 4761 then p:ask(5637, {4766, 4765})
    elseif answer == 4746 or answer == 4747 then p:ask(5631, {4753, 4754})
    elseif answer == 4771 or answer == 4773 then p:ask(5642, {4781, 4780, 4779, 4778})
    elseif answer == 4748 or answer == 4749 or answer == 4767 then p:ask(5632, {4755, 4756})
    elseif answer == 4754 or answer == 4755 or answer == 4778 then p:ask(5636, {4764, 4763})
    elseif answer == 4745 or answer == 4756 or answer == 4757 then p:ask(5630, {4752, 4751})
    elseif answer == 4759 or answer == 4779 or answer == 4781 then p:ask(5629, {4750, 4749})
    end
end

RegisterNPCDef(npc)
